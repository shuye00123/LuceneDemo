/**
 * 
 */
package Lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

/**
 * Lucene
 * @author shuye
 *
 */
public class Demo {
	private String index_path = "D:\\lIndex";
	private String data_path = "D:\\lData";
	
	public String getIndex_path() {
		return index_path;
	}

	public void setIndex_path(String index_path) {
		this.index_path = index_path;
	}

	public String getData_path() {
		return data_path;
	}

	public void setData_path(String data_path) {
		this.data_path = data_path;
	}

	public boolean createIndex(String path){
		Date date1 = new Date();
		List<File> fl = getFileList(path);
		for(File file:fl){
			String fe = file.getName().substring(file.getName().length()-4);
			String content = contentSFactory(fe,file);
			try{
				Analyzer ana = new StandardAnalyzer();
				Directory dir = FSDirectory.open(new File(index_path).toPath());
				
				File indexfile = new File(index_path);
				if(!indexfile.exists()){
					indexfile.mkdirs();
				}
				
				IndexWriterConfig conf = new IndexWriterConfig(ana);
				IndexWriter iw = new IndexWriter(dir, conf);
				
				Document doc = new Document();
				doc.add(new TextField("filename", file.getName(), Store.YES));
				doc.add(new TextField("content", content, Store.YES));
				doc.add(new TextField("path", file.getPath(), Store.YES));
				
				iw.addDocument(doc);
				iw.commit();
				iw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		Date date2 = new Date();
		System.out.println("创建索引耗时："+ (date2.getTime() - date1.getTime()) +"ms\n");
		return true;
	}
	
	public void search(String text){
		Date date1 = new Date();
		try{
			Directory dir = FSDirectory.open(new File(index_path).toPath());
			Analyzer ana = new StandardAnalyzer();
			DirectoryReader dr = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(dr);
			
			QueryParser parser = new QueryParser("content", ana);
			Query query = parser.parse(text);
			
			ScoreDoc[] hits =is.search(query,10).scoreDocs;
			
			for(int i=0; i<hits.length; i++){
				Document hitdoc = is.doc(hits[i].doc);
				System.out.println(hitdoc.get("filename"));
				System.out.println(hitdoc.get("content"));
				System.out.println(hitdoc.get("path"));
				System.out.println(hitdoc.get("---分割线---"));
			}
			dr.close();
			dir.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		Date date2 = new Date();
		System.out.println("搜索耗时："+ (date2.getTime() - date1.getTime()) +"ms\n");
	}
	
	public List<File> getFileList(String path){
		File [] file = new File(path).listFiles();
		List<File> list = new ArrayList<File>();
		for(File temp:file){
			list.add(temp);
		}
		return list;
	}
	
	public String contentSFactory(String fe,File file){
		String ctemp = "";
		switch(fe){
		case "txt":
			ctemp = txt2String(file);
			break;
		case "doc":
			ctemp = doc2String(file);
			break;
		}
		return ctemp;
	}
	
	public String txt2String(File file){
		String result = "";
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s = null;
			while((s=br.readLine()) != null){
				result =result + s +"\n" ;
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public String doc2String(File file){
		String result = "";
		try{
		    FileInputStream fis = new FileInputStream(file);
		    HWPFDocument doc = new HWPFDocument(fis);
		    Range rang = doc.getRange();
		    result += rang.text();
		    fis.close();
		}catch(Exception e){
		    e.printStackTrace();
		}
		 return result;
	}
}
