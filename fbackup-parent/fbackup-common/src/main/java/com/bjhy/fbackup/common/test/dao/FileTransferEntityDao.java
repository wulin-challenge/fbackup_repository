package com.bjhy.fbackup.common.test.dao;

import com.bjhy.fbackup.common.store.derby.JdbcTemplateRepository;

public interface FileTransferEntityDao<P,T> extends JdbcTemplateRepository<P, T> {

}
