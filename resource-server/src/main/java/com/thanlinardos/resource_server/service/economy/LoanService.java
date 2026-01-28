package com.thanlinardos.resource_server.service.economy;

import com.thanlinardos.resource_server.model.mapped.LoanModel;
import com.thanlinardos.resource_server.repository.api.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    @Transactional
    public List<LoanModel> getLoansByPrincipalNameOrderByStartDtDesc(String name) {
        return loanRepository.getByOwnerNameOrderByStartDtDesc(name).stream()
                .map(LoanModel::new)
                .toList();
    }
}
