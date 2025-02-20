export interface Attachment {
    id: number;
    docType: string;
    fileName: string;
}
export interface Device {
    id: number;
    createdOn: Date;
    modifiedOn: Date;
    deviceId: number;
    featureId: number;
    userId: number;
    userType: string;
    userTypeId: string;
    marketingName: string;
    manufacturer: string;
    manufacturingAddress: string;
    modelName: string;
    brandName: string;
    networkTechnologyGSM: string;
    networkTechnologyCDMA: string;
    networkTechnologyEVDO: string;
    networkTechnologyLTE: string;
    networkTechnology5G: string;
    network2GBand: string;
    network3GBand: string;
    network4GBand: string;
    network5GBand: string;
    networkSpeed: string;
    brandDetail: string;
    announceDate: string;
    launchDate: string;
    deviceStatus: string;
    stateInterp: string;
    oem: string;
    organizationId: string;
    allocationDate: string;
    discontinueDate: string;
    simSlot: number;
    imeiQuantity: number;
    simType: string;
    bodyDimension: string;
    bodyWeight: string;
    esimSupport: string;
    softsimSupport: string;
    displayType: string;
    displaySize: string;
    displayResolution: string;
    displayProtection: string;
    os: string;
    osBaseVersion: string;
    memoryInternal: string;
    ram: string;
    memoryCardSlot: string;
    platformCPU: string;
    platformGPU: string;
    mainCameraType: string;
    mainCameraSpec: string;
    mainCameraFeature: string;
    mainCameraVideo: string;
    selfieCameraType: string;
    selfieCameraSpec: string;
    selfieCameraFeature: string;
    selfieCameraVideo: string;
    soundLoudspeaker: string;
    sound35mmJack: number;
    commsWLAN: string;
    commsBluetooth: string;
    commsGPS: string;
    commsUSB: string;
    commsRadio: number;
    commsNFC: number;
    sensor: string;
    colors: string;
    removableUICC: number;
    removableEUICC: number;
    batteryCapacity: number;
    batteryCharging: string;
    launchPriceAsianMarket: number;
    launchPriceUSMarket: number;
    launchPriceEuropeMarket: number;
    launchPriceInternationalMarket: number;
    launchPriceCambodiaMarket: number;
    customPriceOfDevice: number;
    sourceOfCambodiaMarketPrice: string;
    reportedGlobalIssue: string;
    reportedLocalIssue: string;
    selected: boolean;
    attachedFiles: Array<Attachment>;
}