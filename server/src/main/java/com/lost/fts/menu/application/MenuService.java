package com.lost.fts.menu.application;

import com.lost.fts.menu.api.request.*;
import com.lost.fts.menu.api.response.MenuResponse;

import com.lost.fts.user.infrastructure.persistence.User;

import java.util.List;

public interface MenuService {

    MenuResponse createMenu(MenuCreateRequest request);

    MenuResponse updateMenu(
            Long id,
            MenuUpdateRequest request);

    List<MenuResponse> getAllMenus();

    void deactivateMenu(Long id);

    void assignMenusToRole(
            Long roleId,
            RoleMenuRequest request);

    List<MenuResponse> getAccessibleMenus(User user);
}