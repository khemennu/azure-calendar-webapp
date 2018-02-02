package org.nwadiversity.aabrg.connector.documentdb;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import javax.annotation.PostConstruct;

import org.nwadiversity.aabrg.constants.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;

@Component
public class ConnectionManager {
	private final static Logger LOG;

	private CloudStorageAccount storageAccount;

	@Value("${azure.storage.account}")
	private String storageAccountName;
	
	@Value("${azure.storage.key}")
	private String storageKey;
	
	@Value("${azure.documentdb.uri}")
	private String documentDBHost;
	
	@Value("${azure.documentdb.key}")
	private String documentDBKey;
			
	@Autowired
	ApplicationContext context;

	static {
		LOG = LoggerFactory.getLogger(ConnectionManager.class);
	}
	
	
	@PostConstruct
	private void init() {
		String storageConnectionString = String.format(ApplicationConstants.STORAGE_CONNECTION_STRING_FULL,
				storageAccountName, storageKey);

		storageAccount = null;
		try {
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
		} catch (InvalidKeyException | URISyntaxException e) {
			LOG.error("Error generating connection manager...");
			throw new RuntimeException("Error generating connection manager...", e);
		}
	}

	public CloudBlobClient getBlobClient() {
		return storageAccount.createCloudBlobClient();
	}

	public DocumentClient getRWDocClient() {
		DocumentClient documentClient = new DocumentClient(documentDBHost, documentDBKey, ConnectionPolicy.GetDefault(),
				ConsistencyLevel.Session);
		return documentClient;
	}

	public DocumentClient getRDocClient() {
		DocumentClient documentClient = new DocumentClient(documentDBHost, documentDBKey, ConnectionPolicy.GetDefault(),
				ConsistencyLevel.Session);
		return documentClient;
	}
}
