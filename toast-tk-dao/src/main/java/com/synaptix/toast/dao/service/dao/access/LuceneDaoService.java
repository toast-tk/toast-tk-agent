package com.synaptix.toast.dao.service.dao.access;
//package com.mongo.test.service.dao.access;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//import org.apache.commons.beanutils.PropertyUtils;
//import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.tokenattributes.TermAttribute;
//import org.bson.types.ObjectId;
//import org.reflections.ReflectionUtils;
//
//import com.github.jmkgreen.morphia.Datastore;
//import com.github.jmkgreen.morphia.dao.BasicDAO;
//import com.google.inject.Inject;
//import com.google.inject.name.Named;
//import com.mongo.test.domain.impl.LuceneIndexObject;
//import com.mongo.test.service.init.DbStarter;
//import com.mongodb.BasicDBObject;
//
//public class LuceneDaoService extends BasicDAO<LuceneIndexObject, ObjectId> {
//
//	private final Analyzer analyzer;
//
//	public static enum Condition {
//		ALL,
//		IN
//	}
//
//	@Inject
//	public LuceneDaoService(@Named("default_db") String dbName, DbStarter starter, Analyzer analyzer) {
//		super(starter.getDatabaseByName(dbName));
//		this.analyzer = analyzer;
//	}
//
//	/**
//	 * 
//	 * @param ds
//	 * @param collection
//	 * @param objectId
//	 * @param object
//	 */
//	public void index(Datastore ds, String collection, String objectId, Object object) {
//		LuceneIndexObject ob = new LuceneIndexObject();
//		ob.targetCollection = collection;
//		ob.idTargetObject = objectId;
//		String textIndex = "";
//		Set<Field> allFields = ReflectionUtils.getAllFields(object.getClass());
//		for (Field f : allFields) {
//			if (f.getClass().equals(String.class)) {
//				Object property;
//				try {
//					property = PropertyUtils.getProperty(object, f.getName());
//					if (property != null) {
//						textIndex += " " + property.toString();
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		try {
//			ob.indtext = tokenize(analyzer.tokenStream("indtext", new StringReader(textIndex)));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 
//	 * @param query
//	 * @return
//	 */
//	public List<LuceneIndexObject> findHits(String query) {
//		List<String> tokens;
//		try {
//			tokens = tokenize(analyzer.tokenStream("indtext", new StringReader(query)));
//			BasicDBObject qObject = new BasicDBObject(String.format("$%s", Condition.ALL.name().toLowerCase()), tokens.toArray(new String[0]));
//			return this.createQuery().filter("indtext", qObject).asList();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	/**
//	 * 
//	 * @param stream
//	 * @return
//	 * @throws IOException
//	 */
//	private List<String> tokenize(TokenStream stream) throws IOException {
//		List<String> tokens = new ArrayList<String>();
//		TermAttribute term = stream.addAttribute(TermAttribute.class);
//		while (stream.incrementToken()) {
//			tokens.add(term.term());
//		}
//		return tokens;
//	}
//
//}
