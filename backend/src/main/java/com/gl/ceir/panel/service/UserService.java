package com.gl.ceir.panel.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gl.ceir.common.NotificationHelper;
import com.gl.ceir.common.NotificationRepository;
import com.gl.ceir.panel.constant.ActionEnum;
import com.gl.ceir.panel.constant.FeatureEnum;
import com.gl.ceir.panel.constant.HttpStatusEnum;
import com.gl.ceir.panel.constant.LogicalDirectoryEnum;
import com.gl.ceir.panel.constant.MessaeEnum;
import com.gl.ceir.panel.constant.StatusEnum;
import com.gl.ceir.panel.constant.UserTypeEnum;
import com.gl.ceir.panel.dto.AuditTrailDto;
import com.gl.ceir.panel.dto.ChangePasswordDto;
import com.gl.ceir.panel.dto.NotificationDto;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.dto.UserCreateDto;
import com.gl.ceir.panel.dto.UserGroupDto;
import com.gl.ceir.panel.dto.UserGroupPermissionDto;
import com.gl.ceir.panel.dto.UserUpdateDto;
import com.gl.ceir.panel.dto.response.AuthDto;
import com.gl.ceir.panel.dto.response.BooleanDto;
import com.gl.ceir.panel.dto.response.MessageResponse;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.entity.UserGroupEntity;
import com.gl.ceir.panel.entity.UserProfileEntity;
import com.gl.ceir.panel.entity.UserSecurityQuestionEntity;
import com.gl.ceir.panel.entity.UserSecurityQuestionId;
import com.gl.ceir.panel.repository.UserGroupRepository;
import com.gl.ceir.panel.repository.UserProfileRepository;
import com.gl.ceir.panel.repository.UserRepository;
import com.gl.ceir.panel.security.jwt.service.UserDetailsImpl;
import com.gl.ceir.panel.service.criteria.UserCriteriaService;
import com.gl.ceir.panel.util.FileUploadUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor()
@Slf4j
public class UserService {
	@Value("${eirs.panel.source.path:}")
	private String basepath;
	private static UserService instance;
	private final UserRepository userRepository;
	private final UserProfileRepository userProfileRepository;
	private final UserCriteriaService userCriteriaService;
	private final PasswordEncoder encoder;
	private final FileUploadUtil fileUploadUtil;
	private final UserGroupService userGroupService;
	private final AuthenticationManager authenticationManager;
	private final UserPermissionService permissionService;
	private final NotificationRepository notificationRepository;
	private final NotificationHelper notificationHelper;
	private final UserGroupRepository userGroupRepository;
	private final AuditTrailService auditTrailService;
	@Value("${eirs.panel.password.validity:30}")
	private int passwordValidity;
	@Value("${eirs.panel.user.expiration.prompt.days:5}")
	private int userExpirationPromptDays;

	private static final String channelType = "EMAIL";
	@Value("${reset.password.message:Notification}")
	private String resetPasswordMessage;
	@Value("${reset.password.subject:Reset Password}")
	private String resetPasswordSubject;

	@Value("${register.user.message:Please use this username: %s to login the eirs portal}")
	private String registerUserMessage;
	@Value("${register.user.subject:Reset Password}")
	private String registerUserSubject;
	@Value("${eirs.system.admin.support.group:}")
	private String systemAdminSupportGroup;
	@Value("${eirs.customer.care.group:}")
	private String customerCareGroup;

	@PostConstruct
	public void init() {
		instance = this;
	}

	public static UserService getUserService() {
		return instance;
	}

	public List<UserEntity> getUsers() {
		return userRepository.findByCreatedByInAndCurrentStatus(permissionService.permissions().getUserIds(),
				StatusEnum.ACTIVE.status);
	}

	public UserEntity getLoggedInUser() {
		UserEntity entity = null;
		try {
			UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			entity = userRepository.findByUserName(user.getUsername()).get();
		} catch (Exception e) {
			log.info("Error while user regiestered by public api: {}", e.getMessage());
		}
		return entity;
	}

	public ResponseEntity<?> prevalidate() {
		UserEntity entity = getLoggedInUser();
		return new ResponseEntity<>(AuthDto.builder().isLogin(ObjectUtils.isNotEmpty(entity))
				.isPasswordExpire(
						ObjectUtils.isNotEmpty(entity) && entity.getPasswordDate().isBefore(LocalDateTime.now()))
				.build(), HttpStatus.OK);
	}

	public ResponseEntity<?> save(UserCreateDto ucd, HttpServletRequest request) {
		boolean isexisting = false;
		try {
			UserEntity userEntity = null;
			log.info("User detail:{}", ucd);
			ucd.setUserName(ObjectUtils.isNotEmpty(ucd.getUserName()) ? ucd.getUserName() : ucd.getEmail());

			try {
				UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				userEntity = userRepository.findByUserName(user.getUsername()).get();
				isexisting = true;
			} catch (Exception e) {
				log.info("Error while user regiestered by public api: {}", e.getMessage());
			}

			if (ObjectUtils.isNotEmpty(ucd.getPassword()))ucd.setPassword(encoder.encode(ucd.getPassword()));
			String nidlogicalpath = fileUploadUtil.upload(ucd.getNidFile(), basepath, LogicalDirectoryEnum.user.name());
			String idcardlogicalpath = fileUploadUtil.upload(ucd.getIdCardFile(), basepath, LogicalDirectoryEnum.user.name());
			String userlogicalpath = fileUploadUtil.upload(ucd.getPhotoFile(), basepath, LogicalDirectoryEnum.user.name());

			log.info("nid:{},id:{},photo:{}", nidlogicalpath, idcardlogicalpath, userlogicalpath);

			log.info("User detail:{}", ucd);
			UserProfileEntity upe = UserProfileEntity.builder().build();
			BeanUtils.copyProperties(ucd, upe);
			log.info("profile:{}", upe);

			if (ObjectUtils.isNotEmpty(nidlogicalpath))
				upe.setNidFileName(nidlogicalpath);
			if (ObjectUtils.isNotEmpty(idcardlogicalpath))
				upe.setIdCardFileName(idcardlogicalpath);
			if (ObjectUtils.isNotEmpty(userlogicalpath))
				upe.setPhotoFileName(userlogicalpath);

			UserEntity tobesave = UserEntity.builder().build();
			BeanUtils.copyProperties(ucd, tobesave);
			
			if (ObjectUtils.isNotEmpty(ucd.getId())) {
				UserEntity existing = userRepository.findById(ucd.getId()).get();
				upe = upe.toBuilder().id(existing.getProfile().getId()).email(existing.getProfile().getEmail()).build();
				tobesave = tobesave.toBuilder().password(existing.getPassword()).userName(existing.getUserName())
						.passwordDate(existing.getPasswordDate()).roles(existing.getRoles())
						.approvedBy(existing.getApprovedBy()).approvedDate(existing.getApprovedDate())
						.createdBy(existing.getCreatedBy()).currentStatus(existing.getCurrentStatus())
						.failedAttempt(existing.getFailedAttempt()).lastLoginDate(existing.getLastLoginDate())
						.modifiedBy(existing.getModifiedBy()).previousStatus(existing.getPreviousStatus())
						.referenceId(existing.getReferenceId()).remark(existing.getRemark())
						.userLanguage(existing.getUserLanguage()).parent(existing.getParent())
						.questions(existing.getQuestions()).currentStatus(existing.getCurrentStatus()).build();
			} else {
				log.info("User to save:{}", tobesave);
				tobesave = tobesave.toBuilder().currentStatus(StatusEnum.INACTIVE.status)
						.passwordDate(LocalDateTime.now().plusDays(passwordValidity)).userName(RandomStringUtils.randomAlphabetic(8))
						.createdBy(userEntity.getId()).modifiedBy(String.valueOf(userEntity.getId())).build();
			}

			upe = userProfileRepository.save(upe);
			tobesave = userRepository.save(tobesave.toBuilder().profile(upe).build());

			List<UserSecurityQuestionEntity> questions = new ArrayList<>();
			if (ObjectUtils.isNotEmpty(ucd.getQuestion1())) {
				questions.add(UserSecurityQuestionEntity.builder().answer(ucd.getAnswer1()).id(UserSecurityQuestionId
						.builder().questionId(ucd.getQuestion1()).userId(tobesave.getId()).build()).build());
			}
			if (ObjectUtils.isNotEmpty(ucd.getQuestion2())) {
				questions.add(UserSecurityQuestionEntity.builder().answer(ucd.getAnswer2()).id(UserSecurityQuestionId
						.builder().questionId(ucd.getQuestion2()).userId(tobesave.getId()).build()).build());
			}
			if (ObjectUtils.isNotEmpty(ucd.getQuestion3())) {
				questions.add(UserSecurityQuestionEntity.builder().answer(ucd.getAnswer3()).id(UserSecurityQuestionId
						.builder().questionId(ucd.getQuestion3()).userId(tobesave.getId()).build()).build());
			}

			UserEntity savedEntity = userRepository.findByUserName(tobesave.getUserName()).get();
			if (ObjectUtils.isEmpty(userEntity)) userEntity = savedEntity;
			userRepository.save(savedEntity.toBuilder().questions(questions).createdBy(userEntity.getId()).modifiedBy(String.valueOf(userEntity.getId())).build());

			List<UserGroupEntity> groups = userGroupRepository.findByIdUserId(savedEntity.getId());
			if (CollectionUtils.isNotEmpty(groups)) {
				UserGroupDto ugd = UserGroupDto.builder().userId(savedEntity.getId())
						.groups(groups.stream().map(g -> g.getId().getGroupId()).collect(Collectors.toList())).build();
				log.info("User group service:{}", ugd);
				userGroupService.save(ugd,request);
			}
			if (isexisting == false || isexisting == true) {
				try {
					NotificationDto notification = NotificationDto.builder().channelType(channelType)
							.message(String.format(registerUserMessage, tobesave.getUserName()))
							.subject(registerUserSubject).email("kumarssvivek@gmail.com").build();
					log.info("User:{}", tobesave);
					log.info("Notifcation:{}", notification);
					notificationHelper.send(notification);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			notificationRepository.audit(AuditTrailDto.builder().build());
			return ResponseEntity.ok().body(MessageResponse.builder().status(HttpStatusEnum.SUCCESS.status)
					.code(HttpStatus.OK).message(MessaeEnum.SAVE_USER_SUCCESS.message).build());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok().body(MessageResponse.builder().status(HttpStatusEnum.FAILED.status)
					.code(HttpStatus.OK).message(MessaeEnum.SAVE_USER_FAILED.message).build());
		}
	}

	public ResponseEntity<?> update(UserUpdateDto userDto, Long id, HttpServletRequest request) {
		UserEntity group = this.viewById(id);
		log.info("going to update");
		UserCreateDto ucreate = UserCreateDto.builder().build();
		BeanUtils.copyProperties(userDto, ucreate);
		return this.save(ucreate.toBuilder().id(group.getId()).build(),request);
	}

	public ResponseEntity<?> changePassword(ChangePasswordDto cpd, HttpServletRequest request) {
		UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity userEntity = userRepository.findByUserName(user.getUsername()).get();
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(userEntity.getUserName(), cpd.getOldPassword()));
		} catch (Exception e) {
			log.info("Old Password not matched");
			return ResponseEntity.ok().body(MessageResponse.builder().status(HttpStatusEnum.FAILED.status)
					.code(HttpStatus.OK).message(MessaeEnum.OLD_PASS_NOT_MATCHED.message).build());
		}
		if (ObjectUtils.isNotEmpty(cpd.getNewPassword())) {
			userEntity.setPassword(encoder.encode(cpd.getNewPassword()));
			userEntity.setPasswordDate(LocalDateTime.now().plusDays(passwordValidity));
		}
		userRepository.save(userEntity);
		log.info("Password changed successfully");
		
		FeatureEnum feature = FeatureEnum.User;
		ActionEnum action = ActionEnum.PasswordChanged;
		String details = String.format("%s [%s] is %s", feature,userEntity.getId(), action.getName());
		auditTrailService.audit(request, feature, action, details);
		
		return ResponseEntity.ok().body(MessageResponse.builder().status(HttpStatusEnum.SUCCESS.status)
				.code(HttpStatus.OK).message(MessaeEnum.PASS_CHANGE_SUCCESS.message).build());
	}

	public UserEntity viewById(Long id) {
		Optional<UserEntity> uoptional = userRepository.findById(id);
		return uoptional.isPresent() ? uoptional.get() : null;
	}

	public boolean emailExist(String email) {
		return ObjectUtils.isEmpty(userProfileRepository.findByEmail(email)) ? true : false;
	}

	public Page<?> pagination(PaginationRequestDto ulrd) {
		return userCriteriaService.pagination(ulrd);
	}

	public BooleanDto isAlertForPasswordExpire() {
		BooleanDto dto = BooleanDto.builder().build();
		try {
			UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			UserEntity userEntity = userRepository.findByUserName(user.getUsername()).get();
			boolean allow = ObjectUtils.isNotEmpty(userEntity.getPasswordDate())
					&& userEntity.getPasswordDate().isBefore(LocalDateTime.now().plusDays(userExpirationPromptDays));
			log.info("password expiration date:{},allow:{}", userEntity.getPasswordDate(), allow);
			return dto.toBuilder().allow(allow).build();
		} catch (Exception e) {
			log.info("Error while user regiestered by public api: {}", e.getMessage());
		}
		return dto;
	}

	public boolean delete(List<Long> ids, HttpServletRequest request) {
		List<UserEntity> list = userRepository.findByIdIn(ids);
		userRepository.saveAll(list.stream().map(l -> l.toBuilder().currentStatus(StatusEnum.DELETED.status).build())
				.collect(Collectors.toList()));
		
		FeatureEnum feature = FeatureEnum.User;
		ActionEnum action = ActionEnum.Delete;
		String details = String.format("%s [%s] is %s", feature,
				ids.stream().map(Object::toString).collect(Collectors.joining(",")), action.getName());
		auditTrailService.audit(request, feature, action, details);
		
		return true;
	}

	public boolean active(List<Long> ids, HttpServletRequest request) {
		List<UserEntity> list = userRepository.findByIdIn(ids);
		userRepository.saveAll(list.stream().map(l -> l.toBuilder().currentStatus(StatusEnum.ACTIVE.status).build())
				.collect(Collectors.toList()));
		FeatureEnum feature = FeatureEnum.User;
		ActionEnum action = ActionEnum.Active;
		String details = String.format("%s [%s] is %s", feature,
				ids.stream().map(Object::toString).collect(Collectors.joining(",")), action.getName());
		auditTrailService.audit(request, feature, action, details);
		return true;
	}

	public ResponseEntity<?> resetpassword(List<Long> ids, HttpServletRequest request) {
		List<UserEntity> users = userRepository.findByIdIn(ids);
		users.forEach(user -> {
			try {
				NotificationDto notification = NotificationDto.builder().channelType(channelType)
						.message(resetPasswordMessage).subject(resetPasswordSubject).email(user.getProfile().getEmail())
						.build();
				log.info("Notifciation body: {}", notification);
				notificationRepository.sendemail(notification);
				FeatureEnum feature = FeatureEnum.User;
				ActionEnum action = ActionEnum.ResetPassword;
				String details = String.format("%s [%s] is %s", feature,
						ids.stream().map(Object::toString).collect(Collectors.joining(",")), action.getName());
				auditTrailService.audit(request, feature, action, details);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return ResponseEntity.ok().body(
				MessageResponse.builder().message("saveUserSuccess").status("success").code(HttpStatus.OK).build());
	}

	public UserTypeEnum findUserType() {
		UserTypeEnum userType = UserTypeEnum.PUBLIC;
		UserEntity entity = this.getLoggedInUser();
		if (ObjectUtils.isNotEmpty(entity)) {
			List<UserGroupEntity> groups = this.userGroupRepository.findByIdUserId(entity.getId());
			if (CollectionUtils.isNotEmpty(groups)) {
				Set<String> names = groups.stream().map(g -> g.getGroup().getGroupName()).collect(Collectors.toSet());
				userType = names.contains(customerCareGroup) ? UserTypeEnum.CUSTOMER_CARE
						: names.contains(systemAdminSupportGroup) ? UserTypeEnum.TICKET_SUPPORT : UserTypeEnum.ADMIN;
			}
		}
		return userType;
	}

	public ResponseEntity<?> users(String userName) {
		Optional <UserEntity> user = userRepository.findByUserName(userName);
		if(user.isPresent()) {
			log.info("User found:{}, for username:{}", user.get().getId(), userName);
			UserGroupPermissionDto ugpd = permissionService.permissions(user.get());
			log.info("Found userids:{}", ugpd.getUserIds());
			List<Object> users = userRepository.findByIdIn(ugpd.getUserIds());
			log.info("Found valid users for ticket:{}", users);
			return new ResponseEntity<>(users, HttpStatus.OK);
		} else {
			log.warn("User:{} not found in system then return empty list", userName);
			return new ResponseEntity<>(Collections.EMPTY_LIST, HttpStatus.OK);
		}
	}
	public ResponseEntity<?> gropus(Long groupId) {
		List<Long> groups = new ArrayList<>();
		return new ResponseEntity<>(permissionService.groups(groups, groupId), HttpStatus.OK);
	}
}
