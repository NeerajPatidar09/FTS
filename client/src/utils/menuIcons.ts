
import {
  LayoutDashboard,
  Users,
  UsersRound,
  UserPlus,
  UserCheck,
  UserCog,
  Landmark,
  Bell,
  Search,
  List,
  Settings,
  Shield,
  KeyRound,
  Menu,
  Circle,
  BuildingComplexPlus,
  Building,
  ShieldPlus,
} from "lucide-react";

import type { LucideIcon } from "lucide-react";

const menuIconMap: Record<string, LucideIcon> = {
  "layout-dashboard": LayoutDashboard,
  "users": Users,
  "users-round": UsersRound,
  "user-plus": UserPlus,
  "user-check": UserCheck,
  "user-cog": UserCog,
  "landmark": Landmark,
  "bell": Bell,
  "search": Search,
  "list": List,
  "settings": Settings,
  "shield": Shield,
  "key": KeyRound,
  "menu": Menu,
  "building-complex-plus":BuildingComplexPlus,
  "building":Building,
  "shield-plus":ShieldPlus,
};

export const getMenuIcon = (
  iconName?: string
): LucideIcon => {

  if (!iconName) {
    return Circle;
  }

  return menuIconMap[iconName] ?? Circle;
};
