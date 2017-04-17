package jp.alhinc.watanabe_akihiro.calculate_sales;
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


public class CalculateSales {


	//lstファイル読み込みメソッド
	public static boolean lstFileReader(String dirName, String fileName, String kind,
			String regex, HashMap<String, String> nameMap, HashMap<String, Long> salesMap){
		BufferedReader fBuffer = null;

		try{
			if(dirName == null){
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}

		  	File file = new File(dirName, fileName);
		  	if(!file.exists()){
		  		System.out.println(kind+"定義ファイルが存在しません");
		  		return false;
		  	}
		  	FileReader fReader = new FileReader(file);
		  	fBuffer = new BufferedReader(fReader);

		  	String line;

		  	while((line = fBuffer.readLine()) != null){
		  		String s = line;
		  		String fileContent[] = s.split(",", -1);

			  if(fileContent[0].matches(regex) && fileContent.length == 2){
				  nameMap.put(fileContent[0],fileContent[1]);
				  salesMap.put(fileContent[0], 0L);
			  } else{
				  System.out.println(kind+"定義ファイルのフォーマットが不正です");
				  return false;
			  }
		  	}

		} catch(FileNotFoundException e){
			System.out.println(kind+"定義ファイルが見つかりません");
			return false;
		} catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		} finally {
            if (fBuffer != null)try{
            	fBuffer.close();
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return true;
	}

	//outファイル書き込みメソッド

	public static boolean outFileWriter(String dirName, String fileName, HashMap<String, String> nameMap,
			HashMap<String, Long> salesMap){
		BufferedWriter rBuffer = null;
		try{

			File result = new File(dirName, fileName);
			result.createNewFile();
			FileWriter rWriter = new FileWriter(result);
			rBuffer = new BufferedWriter(rWriter);

			List<Map.Entry<String,Long>> Entries
			=new ArrayList<Map.Entry<String,Long>>(salesMap.entrySet());

			Collections.sort(Entries, new Comparator<Map.Entry<String,Long>>(){
				public int compare(Map.Entry<String,Long> entry1, Map.Entry<String,Long> entry2){
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			    }
	        });
			String sep = System.getProperty("line.separator");
			for(Map.Entry<String, Long> b : Entries){
				rBuffer.write(b.getKey()+","+nameMap.get(b.getKey())+","+b.getValue()+sep);
			}
		} catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;

		} finally{
			if (rBuffer != null){
				try {
					rBuffer.close();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					System.out.println("予期せぬエラーが発生しました");
					return false;
				}
		}

		}
		return true;

	}

	public static void main(String[] args){

		HashMap<String, String> branchMap = new HashMap<String, String>();
		HashMap<String, String> commodityMap = new HashMap<String, String>();
		HashMap<String, Long> branchSales = new HashMap<String, Long>();
		HashMap<String, Long> commoditySales = new HashMap<String, Long>();

		//lstファイル読み込み
		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		if(!lstFileReader(args[0], "branch.lst", "支店", "\\d{3}", branchMap, branchSales)){
			return;
		}



		if(!lstFileReader(args[0], "commodity.lst", "商品","[0-9a-zA-Z]{8}", commodityMap, commoditySales)){
			return;
		}



		//売り上げファイル読み込み
			BufferedReader sfBuffer = null;

		try{
			File dir = new File(args[0]);
			File fileList[] = dir.listFiles();

			ArrayList<File> sortedFiles = new ArrayList<File>();


			for(int i = 0; i < fileList.length; i++){
				if(fileList[i].getName().matches("\\d{8}.rcd$") && fileList[i].isFile()) {
					sortedFiles.add(fileList[i]);
				}
			}


			for(int i = 0; i < sortedFiles.size(); i++){
				String fileString = sortedFiles.get(i).getName();
				String fileNumber[] = fileString.split("\\.");
				int fileSequence = Integer.parseInt(fileNumber[0]);
				if(i + 1 != fileSequence){
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}

			}


			for(int i = 0; i < sortedFiles.size(); i++){

				ArrayList<String> factorArray = new ArrayList<String>();

				File salesFiles = sortedFiles.get(i);
				FileReader sfReader = new FileReader(salesFiles);
				sfBuffer = new BufferedReader(sfReader);

				String each;

				while((each = sfBuffer.readLine()) !=null){

					factorArray.add(each);
				}

				if(factorArray.size() != 3){
					System.out.println(sortedFiles.get(i).getName()+"のフォーマットが不正です");
					return;
				}

				long sale = Long.parseLong(factorArray.get(2));

				String bCode =  factorArray.get(0);
				if(branchMap.get(bCode) == null){
					System.out.println(sortedFiles.get(i).getName()+"の支店コードが不正です");
					return;
				}
				long branchBase = branchSales.get(factorArray.get(0));

				long branchSum = branchBase += sale;

				if(branchSum > 1000000000){
					System.out.println("合計金額が10桁を超えました");
					return;
				}

				branchSales.put(factorArray.get(0), branchSum);


				String cCode =  factorArray.get(1);
				if(commodityMap.get(cCode) == null){
					System.out.println(sortedFiles.get(i).getName()+"の商品コードが不正です");
					return;
				}
				long commodityBase = commoditySales.get(factorArray.get(1));

				long commoditySum = commodityBase += sale;

				if(commoditySum > 1000000000){
					System.out.println("合計金額が10桁を超えました");
					return;
				}

				commoditySales.put(factorArray.get(1),commoditySum);

			}

		} catch(FileNotFoundException e){
			System.out.println("ファイルのフォーマットが不正です");
			return;
		} catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		} catch(NumberFormatException e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally{
			if (sfBuffer != null)try{
				sfBuffer.close();
            } catch (IOException e){
				System.out.println("予期せぬエラーが発生しました");
                return;
            }
		}


		//outファイル書き込み


		if(!outFileWriter(args[0], "branch.out", branchMap, branchSales) == true){
			return;
		}

		if(!outFileWriter(args[0], "commodity.out", commodityMap, commoditySales) == true){
			return;
		}


	}

}
