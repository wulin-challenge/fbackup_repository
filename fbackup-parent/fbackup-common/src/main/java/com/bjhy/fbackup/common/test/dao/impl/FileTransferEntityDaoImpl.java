package com.bjhy.fbackup.common.test.dao.impl;

import com.bjhy.fbackup.common.store.derby.JdbcTemplateAbstractRepository;
import com.bjhy.fbackup.common.test.dao.FileTransferEntityDao;
import com.bjhy.fbackup.common.test.domain.FileTransferEntityTest;

public class FileTransferEntityDaoImpl extends JdbcTemplateAbstractRepository<String,FileTransferEntityTest> implements FileTransferEntityDao<String,FileTransferEntityTest>{
}
