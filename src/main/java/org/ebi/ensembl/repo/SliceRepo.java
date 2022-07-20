package org.ebi.ensembl.repo;

import org.ebi.ensembl.repo.handler.ConnectionHandler;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SliceRepo {
    private final ConnectionHandler connectionHandler;

    public SliceRepo(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }


}
