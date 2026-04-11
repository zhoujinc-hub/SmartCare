export interface ElderItem {
  elder_id: number;
  name: string;
  age: number;
  gender: 0 | 1;
  address: string;
  family_contact1: string;
  family_phone1: string;
  family_contact2?: string;
  family_phone2?: string;
  created_at: string;
  physical_notes: string | null;
}

export interface ElderDetailItem extends ElderItem {}

export interface ElderFormData {
  name: string;
  gender: 0 | 1;
  age: number;
  address: string;
  family_contact1: string;
  family_phone1: string;
  family_contact2?: string;
  family_phone2?: string;
}