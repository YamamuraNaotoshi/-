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
		Map(args[0], "branch.lst","[0-9]{3}","[^,].*支店", branchNameMap, branchSaleMap,"支店");
		Map(args[0], "commodity.lst","[0-z]{8}",".*[^,].*", commodityNameMap, commoditySaleMap,"商品");

		// 集計ファイル

		File fileName = new File(args[0]);
		File fileNames[] = fileName.listFiles();
		ArrayList<File> rcdList = new ArrayList<File>();
		ArrayList<String> rcdName = new ArrayList<String>();
		ArrayList<Integer> rcdNum = new ArrayList<Integer>();


		// rcdファイル読み込み

		for (int i = 0; i < fileNames.length; i++) {
			if (fileNames[i].getName().matches("[0-9]{8}\\.rcd$")) {
				rcdList.add(fileNames[i]);
				 File item = fileNames[i];
					if (item.isDirectory()) {
		                System.out.println("連番ではありません");
		                return;
					}
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
				int lineCount = 0;
				ArrayList<String> rcdLine = new ArrayList<String>();
				String str;
				while ((str = br.readLine()) != null) {
					lineCount++;
					rcdLine.add(str);
				}
					if(lineCount != 3){
						System.out.println(fileNames[i]+"のフォーマットが不正です");
						return;
					}

						if(branchSaleMap.containsKey(rcdLine.get(0))){
						}else{
							System.out.println(fileNames[i]+"支店コードが不正です");
							return;
						}
						if(commoditySaleMap.containsKey(rcdLine.get(1))){
						}else{
							System.out.println(fileNames[i]+"商品コードが不正です");
							return;
						}


		// 計算開始
				long branchSum;
				long commoditySum;
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



//branchSaleMap用のソートリスト生成
		List<Map.Entry<String,Long>> branchentries =
						new ArrayList<Map.Entry<String,Long>>(branchSaleMap.entrySet());
		  Collections.sort(branchentries, new Comparator<Map.Entry<String,Long>>() {

			  @Override
			  public int compare(
					Entry<String,Long> entry1, Entry<String,Long> entry2) {
				  return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			  }
		  });

//commoditySaleMap用のソートリスト生成
		List<Map.Entry<String,Long>> commodityentries =
				new ArrayList<Map.Entry<String,Long>>(commoditySaleMap.entrySet());
		  Collections.sort(commodityentries, new Comparator<Map.Entry<String,Long>>() {

			  @Override
			  public int compare(
					Entry<String,Long> entry1, Entry<String,Long> entry2) {
				  return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			  }
		  });
// branch・commodity内容を表示
		  BCout(args[0],"branch.out",branchNameMap,branchentries);
		  BCout(args[0],"commodity.out",commodityNameMap,commodityentries);
	}








//メソッド分け
//支店・商品定義ファイル読み込みメソッド
static void Map(String dir,String filename,String code,String name
		,HashMap<String,String>NameMap,HashMap<String,Long>SaleMap,String error){
	String s;
	try {
		File file = new File(dir, filename); // ファイルの場所
		FileReader fr = new FileReader(file); // fileオブジェクト引数→filereaderオブジェクト作成
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(fr); // filereader→bufferedreaderオブジェクト作成
		while ((s = br.readLine()) != null) { // 文字列をsに代入、その値がnullと比較nullでwhileループ終了
			String[] branchs = s.split(",", 0);
			try {
				if (branchs[0].matches(code) && branchs[1].matches(name) && 2 == branchs.length) {
					NameMap.put(branchs[0], branchs[1]);
					SaleMap.put(branchs[0], 0L);
				} else {
					System.out.println(error+"定義ファイルのフォーマットが不正です");
					return;
				}
			} catch (Exception e) {
				System.out.println(error+"定義ファイルのフォーマットが不正です");
				return;
			}
		}
		//br.close(); // ストリームを閉じる
	} catch (IOException e) {
		System.out.println(error+"定義ファイルのフォーマットが不正です");
		return;
	}
	 catch(ArrayIndexOutOfBoundsException e){
		 System.out.println("予期せぬエラーが発生しました");
		 return;
	} finally {

	}
}


	static boolean BCout(String dir, String fileName, HashMap<String, String> name, List<Map.Entry<String,Long>> sale) {


		try {
			File file = new File(dir, fileName);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			 for (Entry<String,Long> esl : sale) {
				bw.write(esl.getKey() + "," + name.get(esl.getKey()) + "," + esl.getValue());
				bw.newLine();
				System.out.println(esl.getKey() + "," + name.get(esl.getKey()) + "," + esl.getValue());
			}
			bw.close();
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
		}
		return false;

	}

}