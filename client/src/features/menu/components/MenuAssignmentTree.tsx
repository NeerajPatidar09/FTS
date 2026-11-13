
import type { Menu } from "../types/menu.types";

interface MenuAssignmentTreeProps {
  menus: Menu[];
  selectedIds: number[];
  onToggle: (menu: Menu, checked: boolean) => void;
}

interface MenuNodeProps {
  menu: Menu;
  selectedIds: Set<number>;
  onToggle: (menu: Menu, checked: boolean) => void;
  depth: number;
}

function MenuNode({
  menu,
  selectedIds,
  onToggle,
  depth,
}: MenuNodeProps) {
  const checked = selectedIds.has(menu.id);
  const children = menu.children ?? [];

  return (
    <div className="assignment-menu-node">
      <label
        className="assignment-menu-label"
        style={{ paddingLeft: `${depth * 26}px` }}
      >
        <input
          type="checkbox"
          checked={checked}
          disabled={!menu.active}
          onChange={(event) => onToggle(menu, event.target.checked)}
        />

        <span className="assignment-menu-name">
          {menu.menuName}
        </span>

        <span className="assignment-menu-code">
          {menu.menuCode}
        </span>

        {!menu.active && (
          <span className="assignment-inactive">Inactive</span>
        )}
      </label>

      {children.length > 0 && (
        <div className="assignment-menu-children">
          {children.map((child) => (
            <MenuNode
              key={child.id}
              menu={child}
              selectedIds={selectedIds}
              onToggle={onToggle}
              depth={depth + 1}
            />
          ))}
        </div>
      )}
    </div>
  );
}

export default function MenuAssignmentTree({
  menus,
  selectedIds,
  onToggle,
}: MenuAssignmentTreeProps) {
  const selectedSet = new Set(selectedIds);

  return (
    <div className="assignment-menu-tree">
      {menus.map((menu) => (
        <MenuNode
          key={menu.id}
          menu={menu}
          selectedIds={selectedSet}
          onToggle={onToggle}
          depth={0}
        />
      ))}
    </div>
  );
}
