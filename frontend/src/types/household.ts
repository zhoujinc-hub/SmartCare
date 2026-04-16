export interface CommunityItem {
  communityId: number;
  communityName: string;
}

export interface CameraMiniItem {
  cameraId: number;
  cameraName: string;
  locationDesc: string;
  deviceSerial: string;
  statusText: string;
}

export interface HouseholdItem {
  householdId: number;
  householdName: string;
  address: string;
  contact1_name: string;
  contact1_phone: string;
  contact2_name?: string;
  contact2_phone?: string;
  elderList: { elderId: number; name: string }[];
  cameraList: CameraMiniItem[];
  createdAt: string;
}

export type HouseholdFormData = Omit<
  HouseholdItem,
  'householdId' | 'elderList' | 'cameraList' | 'createdAt'
>;

export interface HouseholdFilterParams {
  householdName: string;
  hasCamera?: number;
}

export interface PaginationParams {
  pageNum: number;
  pageSize: number;
  total: number;
}