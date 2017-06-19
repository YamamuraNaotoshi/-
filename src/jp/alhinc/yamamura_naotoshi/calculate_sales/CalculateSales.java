package jp.alhinc.yamamura_naotoshi.calculate_sales;

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
import java.util.Map.Entry;

public class CalculateSales {



	public static void main(String[] args) {
		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		HashMap<String, String> branchNameMap = new HashMap<String, String>(); // 支店コード・支店名
		HashMap<String, String> commodityNameMap = new HashMap<String, String>(); // 商品コード・商品名
		HashMap<String, Long> branchSaleMap = new HashMap<String, Long>(); // 支店コード・売り上げ
		HashMap<String, Long> commoditySaleMap = new HashMap<String, Long>(); // 商品コード・売り上げ

		//支店・商品定義ファイルの読み込み

		if(!fileLoading(args[0], "branch.lst","[0-9]{3}", branchNameMap, branchSaleMap,"支店")){
			return;
		}
		if(!fileLoading(args[0], "commodity.lst","[0-z]{8}", commodityNameMap, commoditySaleMap,"商品")){
			return;
		}


		// 集計ファイル
		File fileName = new File(args[0]);
		File fileNames[] = fileName.listFiles();
		ArrayList<File> rcdList = new ArrayList<File>();
		ArrayList<String> rcdName = new ArrayList<String>();
		ArrayList<Integer> rcdNum = new ArrayList<Integer>();


		// rcdファイル読み込み

		for (int i = 0; i < fileNames.length; i++) {
			if (fileNames[i].getName().matches("[0-9]{8}\\.rcd$") && fileNames[i].isFile()) {
				rcdList.add(fileNames[i]);

				String[] rcdData = fileNames[i].getName().split("\\.");
				rcdName.add(rcdData[0]);
			}
		}

		// 連番
		String[] array=(String[])rcdName.toArray(new String[0]);
		for(int i = 0 ; i < array.length ; i++){
			int rcd = Integer.parseInt(array[i]);
			rcdNum.add(rcd);
		}
		for (int i = 0; i < rcdNum.size()-1; i++) {
			if (rcdNum.get(i)+1  != rcdNum.get(i + 1)) {
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}
		}


		// 集計
		for (int i = 0; i < rcdList.size(); i++) {
			BufferedReader br = null;
			try {
				FileReader fr = new FileReader(rcdList.get(i));
				br = new BufferedReader(fr);
				ArrayList<String> rcdLine = new ArrayList<String>();
				String str;
				while ((str = br.readLine()) != null) {
					rcdLine.add(str);
				}
				if(rcdLine.size() != 3){
					System.out.println(fileNames[i].getName()+"のフォーマットが不正です");
					return;
				}

				if(!branchSaleMap.containsKey(rcdLine.get(0))){
					System.out.println(fileNames[i].getName()+"の支店コードが不正です");
					return;
				}
				if(!commoditySaleMap.containsKey(rcdLine.get(1))){
					System.out.println(fileNames[i].getName()+"の商品コードが不正です");
					return;
				}
				// 計算開始
				long branchSum;
				long commoditySum;
				//数値以外エラー
				if(!rcdLine.get(2).matches("[0-9]*$")){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
				//branchの計算
				if (branchSaleMap.containsKey(rcdLine.get(0))) {
					branchSum = branchSaleMap.get(rcdLine.get(0));
					branchSum += Long.parseLong(rcdLine.get(2));
					if (String.valueOf(branchSum).matches("^[0-9]{1,10}")) {
						branchSaleMap.put(rcdLine.get(0), branchSum);
					} else {
						System.out.println("合計金額が10桁を超えました");
						return;
					}

				} else {
					branchSum = Long.parseLong(rcdLine.get(2));
					branchSaleMap.put(rcdLine.get(0), branchSum);
				}

				//commodityの計算
				if (commoditySaleMap.containsKey(rcdLine.get(1))) {
					commoditySum = commoditySaleMap.get(rcdLine.get(1));
					commoditySum += Long.parseLong(rcdLine.get(2));
					if (String.valueOf(commoditySum).matches("^[0-9]{1,10}")) {
						commoditySaleMap.put(rcdLine.get(1), commoditySum);
					} else {
						System.out.println("合計金額が10桁を超えました");
						return;
					}
				} else {
					commoditySum = Long.parseLong(rcdLine.get(2));
					commoditySaleMap.put(rcdLine.get(1), commoditySum);
				}

			} catch (FileNotFoundException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			}
		}


		  // branch・commodity内容を表示
		 if(!fileCreating(args[0],"branch.out",branchNameMap,branchSaleMap)) {
			 return;
		 }
		 if(!fileCreating(args[0],"commodity.out",commodityNameMap,commoditySaleMap)){
			 return;
		 }
	}




	//メソッド分け
	//支店・商品定義ファイル読み込みメソッド
	static boolean fileLoading(String dir,String filename,String code
			,HashMap<String,String>NameMap,HashMap<String,Long>SaleMap,String error){
		String s;
		BufferedReader br = null;
		try {
			File file = new File(dir, filename); // ファイルの場所
			if(!file.exists()){
				System.out.println(error+"定義ファイルが存在しません");
				return false;
			}
			FileReader fr = new FileReader(file); // fileオブジェクト引数→filereaderオブジェクト作成
			br = new BufferedReader(fr); // filereader→bufferedreaderオブジェクト作成
			while ((s = br.readLine()) != null) { // 文字列をsに代入、その値がnullと比較nullでwhileループ終了
				String[] fileStrig = s.split(",", 0);

				if (fileStrig[0].matches(code)  && 2 == fileStrig.length) {
					NameMap.put(fileStrig[0], fileStrig[1]);
					SaleMap.put(fileStrig[0], 0L);
				} else {
					System.out.println(error+"定義ファイルのフォーマットが不正です");
					return false;
				}
			}
		} catch (IOException e) {
			System.out.println(error+"定義ファイルのフォーマットが不正です");
			return false;
		} finally {
			try {
				if(br != null){
					br.close();
				}
			} catch (IOException e) {
				 System.out.println("予期せぬエラーが発生しました");
				 return false;
			}
		}
		return true;
	}


	static boolean fileCreating(String dir, String fileName
			, HashMap<String, String> nameMap,HashMap<String, Long> saleMap ) {
		BufferedWriter bw = null;

		List<Map.Entry<String,Long>> entries =
			new ArrayList<Map.Entry<String,Long>>(saleMap.entrySet());
		 Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {
			  @Override
			  public int compare(
				Entry<String,Long> entry1, Entry<String,Long> entry2) {
				  return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			  	}
		  });
		try {
			File file = new File(dir, fileName);
			bw = new BufferedWriter(new FileWriter(file));
			 for (Entry<String,Long> esl : entries) {
				bw.write(esl.getKey() + "," + nameMap.get(esl.getKey()) + "," + esl.getValue());
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}
		finally{
			try {
				if(bw!= null){
					bw.close();
				}
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return true;

	}

}