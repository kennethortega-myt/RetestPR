export interface AccessibilityItem {
  icon: string;
  steps: AccessibilityStep[];
  stepActive: AccessibilityStep;
}

export interface AccessibilityStep {
  id: string;
  description: string;
  icon?: string;
}

export interface Language {
  code: string;
  name: string;
  flag: string;
}

export interface Profile {
  id: string;
  name: string;
  icon: string;
}
