package cn.mcplugin.kqjcq;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SickWebSite implements Runnable{
	public static boolean testJiangSu = true;
	public static String staticHtml = null;
	public SickWebSite(){
		try {
			getHtmlPageResponse();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}
	public static void getHtmlPageResponse() throws IOException {
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常
		webClient.getOptions().setActiveXNative(false);
		//webClient.getOptions().setCssEnabled(false);//是否启用CSS
		webClient.getOptions().setJavaScriptEnabled(true); //很重要，启用JS
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX

		webClient.getOptions().setTimeout(30000);//设置“浏览器”的请求超时时间
		webClient.setJavaScriptTimeout(30000);//设置JS执行的超时时间
		HtmlPage page;
		try {
			page = webClient.getPage("https://voice.baidu.com/act/newpneumonia/newpneumonia");
		} catch (Exception e) {
			webClient.close();
			//e.printStackTrace();
			throw e;
		}
		//webClient.waitForBackgroundJavaScript(30000);//该方法阻塞线程

		staticHtml = page.asXml();
		webClient.close();
	}
	//解析xml获取ImageUrl地址
	public static String getImageUrl(){
		Document document = Jsoup.parse(staticHtml);//获取html文档
		Elements es = document.getElementsByTag("img");//获取元素节点等
		for(Element e : es) {
			if(e.attr("alt").equals("疫情地图")) {
				return e.attr("src");
			}
		}
		return null;
	}

	public static String getSickInfo() throws IOException{
		getHtmlPageResponse();
		String document = Jsoup.parse(staticHtml).text();//获取html文档
		int a =document.indexOf("数据更新至");
		int b = document.indexOf("国内");
		String first = document.substring(a,b);
		int c = document.indexOf("疫情 治愈");
		String second = document.substring(b,c);
		int d = document.indexOf("查看迁徙地图");
		String thrid = document.substring(c,d);
		return first+"\n"+second+"\n"+thrid;
	}
	public static String getArea(String area) throws IOException{
		if(staticHtml == null) {
			getHtmlPageResponse();
		}
		String document = Jsoup.parse(staticHtml).text();//获取html文档
		int a = document.indexOf(area);
		int b = document.indexOf("国外");
		String fin = document.substring(a,b);
		String[] arr = fin.split("\\s+");
		String areaInfo = null;
		if(arr.length > 0) {
			for(int i = 0; i<arr.length;i++) {
				if(arr[i].equals(area)) {
					areaInfo = arr[i];
					if(arr[i+1].matches("\\d+")) {
						areaInfo = areaInfo+"确诊："+arr[i+1]+"例";
						return areaInfo;
					}
				}
			}
			
		}
		return "没有找到"+area+"的疫情信息";
	}

	public static void main(String[] args) throws IOException {
		SickWebSite sws = new SickWebSite();
		System.out.println(getArea("浙江"));
		//		Thread t = new Thread(sws);
		//		t.start();
	}
		@Override
		public void run() {
			try {
				getHtmlPageResponse();
				Thread.sleep(600000);
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
		}
	
}