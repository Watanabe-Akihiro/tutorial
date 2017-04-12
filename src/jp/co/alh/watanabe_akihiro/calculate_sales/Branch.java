package jp.co.alh.watanabe_akihiro.calculate_sales;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Branch{
	public static void main(String[] args){
		//try内で宣言した変数は外では使えない→全体で使う変数はtryの外で定義する
		//mapは全部で4つ　支店で2個　商品で2個
		 HashMap<String, String> branchMap = new HashMap<String, String>();
		 HashMap<String, String> commodityMap = new HashMap<String, String>();
		 HashMap<String, Long> branchSales = new HashMap<String, Long>();
		 HashMap<String, Long> commoditySales = new HashMap<String, Long>();

		try{
			  File file = new File(args[0]+"\\branch.lst");
			  FileReader fr = new FileReader(file);
			  BufferedReader br = new BufferedReader(fr);


			  String line;

			  while((line = br.readLine()) != null){
				  String s = line;
				  String store[] = s.split(",");

				  if(store[0].length()<3){
					  System.out.println("支店定義ファイルのフォーマットが不正です");
				  }else{
					  branchMap.put(store[0],store[1]);
					  branchSales.put(store[0], 0L);
				  }
			  }

			  br.close();



			}catch(FileNotFoundException e){
			  System.out.println("支店定義ファイルが見つかりません");
			}
		catch(IOException e){
			System.out.println(e);
		}


		System.out.println(branchMap.entrySet());

		try{
			File f = new File(args[0]+"\\commodity.lst");
			FileReader fire = new FileReader(f);
			BufferedReader bur = new BufferedReader(fire);

			String l;
			while((l = bur.readLine()) != null){
				String str = l;
				String product[] = str.split(",");
				if(product[0].length()<7){
					System.out.println("商品定義ファイルのフォーマットが不正です");
				}else{
					commodityMap.put(product[0],product[1]);
					commoditySales.put(product[0], 0L);
				}
			}
			 bur.close();
			}catch(FileNotFoundException e){
				  System.out.println("商品定義ファイルが見つかりません");
				}
			catch(IOException e){
				System.out.println(e);
			}

		System.out.println(commodityMap.entrySet());

		try{
			//ディレクトリから拡張子.rcdを含むファイルを抽出
			File dir = new File(args[0]);
			File fileList[] = dir.listFiles();

			//ArrayListに.rcdを含むファイルを入れる
			ArrayList<File> sortedFiles = new ArrayList<File>();


			for(int i=0; i<fileList.length; i++){
				if(fileList[i].getName().contains(".rcd")) {
					sortedFiles.add(fileList[i]);

				}
			}

			for(int n=0; n<sortedFiles.size(); n++){
				ArrayList<String> factorArray = new ArrayList<String>();
				File salesFiles = sortedFiles.get(n);
				FileReader sfReader = new FileReader(salesFiles);
				BufferedReader sfBuffer = new BufferedReader(sfReader);

				String each;

					while((each = sfBuffer.readLine()) !=null){

						factorArray.add(each);
					}


					long sale = Long.parseLong(factorArray.get(2));


					long branchBase = branchSales.get(factorArray.get(0));

					long branchSum = branchBase += sale;

					long commodityBase = commoditySales.get(factorArray.get(1));

					long commoditySum = commodityBase += sale;

					branchSales.put(factorArray.get(0), branchSum);

					commoditySales.put(factorArray.get(1),commoditySum);

			sfBuffer.close();
			}


		}catch(FileNotFoundException e){
				  System.out.println("ファイルのフォーマットが不正です");
		}catch(IOException e){
				System.out.println(e);
			}
		System.out.println(branchSales.entrySet());
		System.out.println(commoditySales.entrySet());


	}

}
