package pishen.dblp;


public class Main {
	public static void main(String[] args) {
		new Controller().start();
		
		/*
		try {
			URL test = new URL("http://dx.doi.org/10.3991/ijet.v3i1.750");
			URLConnection con = test.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:17.0) Gecko/20100101 Firefox/17.0");
			System.out.println("content-type=" + con.getContentType());
			System.out.println("url=" + con.getURL());
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while((line = in.readLine()) != null){
				System.out.println(line);
			}
			in.close();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
}
