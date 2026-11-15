package com.lost.fts.investigation.application;

import com.lost.fts.investigation.infrastructure.persistence.Investigation;
// import com.lost.
import java.util.List;

public interface InvestigationService {

    public Investigation getById(Long id);

    public List<Investigation> getAccessibleInvestigations();
}
