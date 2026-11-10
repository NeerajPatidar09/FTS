package com.lost.fts.menu.api.response;

import java.util.List;

public record MenuResponse(

    Long id,

    String menuCode,

    String menuName,

    String menuPath,

    String menuIcon,

    Long parentId,

    String requiredPermission,

    Integer displayOrder,

    boolean active,

    List<MenuResponse> children

) {}