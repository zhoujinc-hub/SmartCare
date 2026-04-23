export interface ElderItem {
    elderId: number;
    name: string;
    age: number;
    gender: 0 | 1;
    address: string;
    familyContact1: string;
    familyPhone1: string;
    familyContact2?: string;
    familyPhone2?: string;
    createdAt: string;
    healthNotes: string | null;
    [key: string]: any;
}

export interface ElderDetailItem extends ElderItem {}

export interface ElderFormData extends Omit<ElderItem, 'elderId' | 'created_at'> {}

