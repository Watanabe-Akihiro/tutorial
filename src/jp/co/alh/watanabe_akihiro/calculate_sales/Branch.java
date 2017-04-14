package jp.co.alh.watanabe_akihiro.calculate_sales;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Branch {


	public static void main(String[] args){
		HashMap<String, String> branchMap = new HashMap<String, String>();
		HashMap<String, String> commodityMap = new HashMap<String, String>();
		HashMap<String, Long> branchSales = new HashMap<String, Long>();
		HashMap<String, Long> commoditySales = new HashMap<String, Long>();

		BufferedReader br = null;

		try{
			if(args == null || args.length != 1 ){
				System.out.println("予期せぬエラーが発生しました");
				return;
	  		}

		  	File file = new File(args[0]+"\\branch.lst");
		  	if(!file.exists()){
		  		System.out.println("支店定義ファイルが存在しません");
		  		return;
		  	}
		  	FileReader fr = new FileReader(file);
		  	br = new BufferedReader(fr);

		  	String line;

		  	while((line = br.readLine()) != null){
			  String s = line;
			  String store[] = s.split(",");

			  if(store[0].matches("\\d{3}")){
				  branchMap.put(store[0],store[1]);
				  branchSales.put(store[0], 0L);
			  }else{
				  System.out.println("支店定義ファイルのフォーマットが不正です");
				  return;
			  }
		  	}

		}catch(FileNotFoundException e){
			System.out.println("支店定義ファイルが見つかりません");
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}finally {
            if (br != null)try{
            	br.close();
			}
            catch (IOException e) {
				e.printStackTrace();
			}
		}


	    BufferedReader bur = null;

		try{
			File f = new File(args[0]+"\\commodity.lst");
			bur = new BufferedReader(new FileReader(f));

			String l;
			while((l = bur.readLine()) != null){
				String str = l;
				String product[] = str.split(",");
			    if(product[0].matches("[0-9a-zA-Z]{8}")){
			    	commodityMap.put(product[0],product[1]);
					commoditySales.put(product[0], 0L);
				}else{
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
			}
		}
		catch(FileNotFoundException e){
			 System.out.println("商品定義ファイルが見つかりません");
		}
		catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}finally{
			if (bur != null)try {
				bur.close();
            }
			catch (IOException e){
	                    e.printStackTrace();
	        }
		}

		//ここからrcdファイル読み込み

			BufferedReader sfBuffer = null;

		try{
			//ディレクトリから拡張子.rcdを含むファイルを抽出
			File dir = new File(args[0]);
			File fileList[] = dir.listFiles();

			//ArrayListに.rcdを含むファイルを入れる
			ArrayList<File> sortedFiles = new ArrayList<File>();


			for(int i=0; i<fileList.length; i++){
				if(fileList[i].getName().matches("\\d{8}.rcd$") && fileList[i].isFile()) {
					sortedFiles.add(fileList[i]);
				}
			}


			for(int g=0; g<sortedFiles.size(); g++){
				String fileString = sortedFiles.get(g).getName();
				String fileNumber[] = fileString.split("\\.");
				int fileSequence = Integer.parseInt(fileNumber[0]);
				if(g+1 != fileSequence){
					System.out.println("売り上げファイル名が連番になっていません");
					return;
				}

			}




			for(int n=0; n<sortedFiles.size(); n++){
				//読み込んだ文字列を格納
				ArrayList<String> factorArray = new ArrayList<String>();

				File salesFiles = sortedFiles.get(n);
				FileReader sfReader = new FileReader(salesFiles);
				sfBuffer = new BufferedReader(sfReader);

				String each;

				while((each = sfBuffer.readLine()) !=null){

					factorArray.add(each);
				}

				long sale = Long.parseLong(factorArray.get(2));

				String bCode =  factorArray.get(0);
				if(branchMap.get(bCode) == null){
					System.out.println(sortedFiles.get(n).getName()+"の支店コードが不正です");
					return;
				}
				long branchBase = branchSales.get(factorArray.get(0));

				long branchSum = branchBase += sale;

				if(branchSum>1000000000){
					System.out.println("合計が10桁を超えています");
					return;
				}

				branchSales.put(factorArray.get(0), branchSum);


				String cCode =  factorArray.get(1);
				if(commodityMap.get(cCode) == null){
					System.out.println(sortedFiles.get(n).getName()+"の支店コードが不正です");
					return;
				}
				long commodityBase = commoditySales.get(factorArray.get(1));

				long commoditySum = commodityBase += sale;

				if(commoditySum>1000000000){
					System.out.println("合計が10桁を超えています");
					return;
				}

				commoditySales.put(factorArray.get(1),commoditySum);

			}

		}
		catch(FileNotFoundException e){
			System.out.println("ファイルのフォーマットが不正です");
		}
		catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}finally{
			if (sfBuffer != null)try{
				sfBuffer.close();
            }
			catch (IOException e){
                e.printStackTrace();
            }
		}

			//ここまで売り上げファイル読み込み


		//ここからoutファイル書き込み

		try{
			File branchResult = new File(args[0]+"\\branch.out");
			branchResult.createNewFile();
			FileWriter brWriter = new FileWriter(branchResult);
			BufferedWriter brBuffer = new BufferedWriter(brWriter);

			List<Map.Entry<String,Long>> bsEntries
			=new ArrayList<Map.Entry<String,Long>>(branchSales.entrySet());

			Collections.sort(bsEntries, new Comparator<Map.Entry<String,Long>>(){
				public int compare(Map.Entry<String,Long> entry1, Map.Entry<String,Long> entry2){
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			    }
	        });

			for(Map.Entry<String, Long> b : bsEntries){
				brBuffer.write(b.getKey()+","+branchMap.get(b.getKey())+","+b.getValue()+"\r\n");
			}

			File commodityResult = new File(args[0]+"\\commodity.out");
			commodityResult.createNewFile();
			FileWriter crWriter = new FileWriter(commodityResult);
			BufferedWriter crBuffer = new BufferedWriter(crWriter);

			List<Map.Entry<String,Long>> csEntries
			=new ArrayList<Map.Entry<String,Long>>(commoditySales.entrySet());

			Collections.sort(csEntries, new Comparator<Map.Entry<String,Long>>(){
				public int compare(Map.Entry<String,Long> entry3, Map.Entry<String,Long> entry4){
					return ((Long)entry4.getValue()).compareTo((Long)entry3.getValue());
	            }
	        });

			for(Map.Entry<String, Long> c : csEntries){
				crBuffer.write(c.getKey()+","+commodityMap.get(c.getKey())+","+c.getValue()+"\r\n");
			}
			brBuffer.close();
			crBuffer.close();

		}
		catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}

		//ここまでoutファイル書き込み

	}

}
