import {
  LayoutDashboard,
  Users,
  UsersRound,
  List,
  UserPlus,
  UserCheck,
  UserCog,
  Landmark,
  Bell,
  Search,
  Settings,
  Shield,
  ShieldPlus,
  Key,
  KeyRound,
  Menu as MenuIcon,
  Building,
  Folder,
  FileText,
} from "lucide-react";

interface MenuIconPickerProps {
  iconName: string | null;
  size?: number;
}

const iconMap = {
  "layout-dashboard": LayoutDashboard,
  users: Users,
  "users-round": UsersRound,
  list: List,
  "user-plus": UserPlus,
  "user-check": UserCheck,
  "user-cog": UserCog,
  landmark: Landmark,
  bell: Bell,
  search: Search,
  settings: Settings,
  shield: Shield,
  "shield-plus": ShieldPlus,
  key: Key,
  "key-round": KeyRound,
  menu: MenuIcon,
  building: Building,
  folder: Folder,
  "file-text": FileText,
} as const;

export default function MenuIconPicker({
  iconName,
  size = 19,
}: MenuIconPickerProps) {

  const Icon = iconName
    ? iconMap[iconName as keyof typeof iconMap]
    : undefined;

  return Icon ? (
    <Icon size={size} />
  ) : (
    <Folder size={size} />
  );
}
