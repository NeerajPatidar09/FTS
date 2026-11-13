
import type { MenuResponse } from "../features/auth/types/auth.types";

interface MenuFilterOptions {
  permissions: string[];
}

export const filterMenusByPermission = (
  menus: MenuResponse[],
  options: MenuFilterOptions
): MenuResponse[] => {

  const permissionSet = new Set(options.permissions);

  return [...(menus ?? [])]
    .filter((menu) => menu.active)
    .sort((a, b) => a.displayOrder - b.displayOrder)
    .reduce<MenuResponse[]>((result, menu) => {

      // Check parent menu permission
      if (
        menu.requiredPermission &&
        !permissionSet.has(menu.requiredPermission)
      ) {
        return result;
      }

      // Recursively filter children 
      const children = filterMenusByPermission(
        menu.children ?? [],
        options
      );

      // Hide parent when it has no path and no accessible children
      if (!menu.menuPath && children.length === 0) {
        return result;
      }

      result.push({
        ...menu,
        children,
      });

      return result;

    }, []);
};
