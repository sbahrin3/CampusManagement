package portal;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import portal.entity.News;


public class NewsModule extends LebahModule {
	
	private String path = "apps/portal/news/";
	private DbPersistence db = new DbPersistence(); 
	private String userId = "";
	
	public void setUserId(String uid) {
		userId = uid;
	}
	
	public String getUserId() {
		return userId;
	}
	
	@Override
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}
	
	@Override
	public String start() {

		getNumberOfNews();
		
		listNews(1);

		return path + "start.vm";
	}
	
	private void getNumberOfNews() {
		int pageSize = 10;
		try {
			pageSize = Integer.parseInt(request.getParameter("page_size"));
		} catch ( Exception e ) {}
		
		String sql = "select count(n) from News n";
		
		long size = (Long) db.get(sql);
		context.put("total_count", size);
		
		long numPages = size / pageSize;
		long more = size % pageSize;
		if ( more > 0 ) {
			numPages = numPages + 1;
		}
		context.put("total_page", (int) numPages);
	}
	
	@Command("list_news")
	public String listNews(int pageNum) {
		int pageSize = 10;
		try {
			pageSize = Integer.parseInt(request.getParameter("page_size"));
		} catch ( Exception e ) {}
		context.put("page_size", pageSize);
		
		int start = 0;
		if ( pageNum > 1 ) {
			start = (pageNum - 1) * pageSize;
		}
		context.put("page_num", pageNum);

		String sql = "select n from News n order by n.createDate desc, n.createTime desc";
		List<News> list = db.list(sql, start, pageSize);
		context.put("news_list", list);
		
		return path + "list_news.vm";
	}
	
	@Command("next_page")
	public String nextPage() throws Exception {
		int pageNum = 0;
		try {
			pageNum = Integer.parseInt(request.getParameter("page_num"));
			pageNum++;
		} catch ( Exception e ) {}
		
		listNews(pageNum);
		
		return path + "list_news.vm";
		
	}
	
	@Command("prev_page")
	public String prevPage() throws Exception {
		
		int pageNum = 0;
		try {
			pageNum = Integer.parseInt(request.getParameter("page_num"));
			pageNum--;
		} catch ( Exception e ) {}
		
		listNews(pageNum);
		
		return path + "list_news.vm";
	}
	
	@Command("go_page")
	public String gotoPage() throws Exception {
		

		int pageNum = 0;
		try {
			pageNum = Integer.parseInt(request.getParameter("page"));
		} catch ( Exception e ) {}
		
		listNews(pageNum);
		
		return path + "list_news.vm";
		
	}
	
	@Command("open_news")
	public String openNews() throws Exception {
		
		String newsId = request.getParameter("news_id");
		News news = db.find(News.class, newsId);
		context.put("news", news);
		
		return path + "open_news.vm";
	}
	
	@Command("delete_news")
	public String deleteNews() throws Exception {
		
		String newsId = request.getParameter("news_id");
		News news = db.find(News.class, newsId);
		
		db.begin();
		db.remove(news);
		db.commit();
		
		int pageNum = 0;
		try {
			pageNum = Integer.parseInt(request.getParameter("page"));
		} catch ( Exception e ) {}
		
		listNews(pageNum);
		
		return path + "list_news.vm";
	}
	
	@Command("add_news")
	public String addNews() throws Exception {
		
		String newsId = request.getParameter("news_id");
		if ( newsId != null && !"".equals(newsId)) {
			News news = db.find(News.class, newsId);
			context.put("news", news);
		}
		else
			context.remove("news");
		
		return path + "news.vm";
	}
	
	@Command("save_news")
	public String saveNews() throws Exception {
		
		String newsId = request.getParameter("news_id");
		String text = request.getParameter("text");
		News news = null;
		if ( newsId == null || "".equals(newsId)) {
			news = new News();
			db.begin();
			news.setText(text);
			news.setCreateDate(new Date());
			news.setCreateTime(new Date());
			news.setUserId(userId);
			db.persist(news);
			db.commit();
		}
		else {
			news = db.find(News.class, newsId);
			db.begin();
			news.setText(text);
			news.setModifyDate(new Date());
			news.setModifyTime(new Date());
			news.setModifyBy(userId);
			db.commit();
		}
		
		context.put("news", news);
		
		return path + "open_news.vm";
	}
	
	

}
