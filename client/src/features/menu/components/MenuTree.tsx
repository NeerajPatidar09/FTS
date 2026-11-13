
import { ChevronDown, ChevronRight } from "lucide-react";
import { useState } from "react";

import type { Menu } from "../types/menu.types";

import MenuIconPicker from "./MenuIconPicker";

// interface MenuTreeProps {
//   menus: Menu[];
//   level?: number;
//   onDeactivate: (menu: Menu) => void;
// }

interface MenuTreeProps {
  menus: Menu[];
  onDeactivate: (menuId: number) => void;
  actionLoading?: boolean;
}


export default function MenuTree({
  menus,
  level = 0,
  onDeactivate,
}: MenuTreeProps) {

  const [expanded, setExpanded] = useState<number[]>([]);

  const toggleExpand = (id: number) => {
    setExpanded((previous) =>
      previous.includes(id)
        ? previous.filter((item) => item !== id)
        : [...previous, id]
    );
  };

  return (
    <div className="menu-tree">

      {menus.map((menu) => {

        const hasChildren = menu.children?.length > 0;

        const isExpanded = expanded.includes(menu.id);

        return (
          <div className="menu-tree-node" key={menu.id}>

            <div
              className="menu-tree-row"
              style={{ paddingLeft: `${16 + level * 28}px` }}
            >

              <button
                type="button"
                className="menu-tree-expand"
                onClick={() => toggleExpand(menu.id)}
                disabled={!hasChildren}
              >
                {hasChildren ? (
                  isExpanded
                    ? <ChevronDown size={16} />
                    : <ChevronRight size={16} />
                ) : null}
              </button>

              <div className="menu-tree-icon">
                <MenuIconPicker iconName={menu.menuIcon} />
              </div>

              <div className="menu-tree-details">

                <strong>{menu.menuName}</strong>

                <span>{menu.menuCode}</span>

              </div>

              <div className="menu-tree-path">
                {menu.menuPath || "Parent Menu"}
              </div>

              <span
                className={`menu-status ${
                  menu.active ? "active" : "inactive"
                }`}
              >
                {menu.active ? "Active" : "Inactive"}
              </span>

              {menu.active && (
                <button
                  type="button"
                  className="menu-deactivate-btn"
                  onClick={() => onDeactivate(menu)}
                >
                  Deactivate
                </button>
              )}

            </div>

            {hasChildren && isExpanded && (
              <MenuTree
                menus={menu.children}
                level={level + 1}
                onDeactivate={onDeactivate}
              />
            )}

          </div>
        );
      })}

    </div>
  );
}
