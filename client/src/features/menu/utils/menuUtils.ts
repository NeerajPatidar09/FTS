import type { Menu } from "../types/menu.types";

// Get all menus recursively
export const flattenMenus = (menus: Menu[]): Menu[] => {
  return menus.flatMap((menu) => [
    menu,
    ...flattenMenus(menu.children ?? []),
  ]);
};

// Find a menu by ID
export const findMenuById = (
  menus: Menu[],
  id: number
): Menu | undefined => {

  for (const menu of menus) {

    if (menu.id === id) {
      return menu;
    }

    const found = findMenuById(menu.children ?? [], id);

    if (found) {
      return found;
    }
  }

  return undefined;
};

// Count all menus recursively
export const countMenus = (menus: Menu[]): number => {
  return menus.reduce(
    (total, menu) =>
      total + 1 + countMenus(menu.children ?? []),
    0
  );
};

// Count active menus recursively
export const countActiveMenus = (menus: Menu[]): number => {
  return menus.reduce(
    (total, menu) =>
      total +
      (menu.active ? 1 : 0) +
      countActiveMenus(menu.children ?? []),
    0
  );
};
