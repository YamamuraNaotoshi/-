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

		String s;
		String set;
		String str;

		HashMap<String, String> branchNameMap = new HashMap<String, String>(); // 支店コード・支店名
		HashMap<String, String> commodityNameMap = new HashMap<String, String>(); // 商品コード・商品名
		HashMap<String, Long> branchSaleMap = new HashMap<String, Long>(); // 支店コード・売り上げ
		HashMap<String, Long> commoditySaleMap = new HashMap<String, Long>(); // 商品コード・売り上げ

		// 支店定義ファイルの読み込み
		System.out.println("【支店定義ファイルを読み込み】");
		try {
			File file = new File(args[0], "branch.lst"); // ファイルの場所
			FileReader fr = new FileReader(file); // fileオブジェクト引数→filereaderオブジェクト作成
			BufferedReader br = new BufferedReader(fr); // filereader→bufferedreaderオブジェクト作成
			while ((s = br.readLine()) != null) { // 文字列をsに代入、その値がnullと比較nullでwhileループ終了
				String[] branchs = s.split(",", 0);
				try {
					if (branchs[0].matches("[0-9]{3}") && branchs[1].matches("[^,].*") && 2 == branchs.length) {
						// branchs[0]の文字列が数字（0～9）の3桁であるか
						// branchs[1]の文字列に","が含まれているか
						// branchsの文字列数が2つである場合
						branchNameMap.put(branchs[0], branchs[1]);
						System.out.println(branchs[0] + "," + branchNameMap.get(branchs[0]));
						branchSaleMap.put(branchs[0], 0L);
					} else {
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
				} catch (Exception e) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
				}
			}
			br.close(); // ストリームを閉じる
		} catch (IOException e) {
			System.out.println("支店定義ファイルが存在しません");
		} finally {
			System.out.println("【支店定義ファイルの読み込みが終わりました】");
		}

		// 商品定義ファイルの読み込み
		System.out.println("【商品定義ファイルを読み込む】");
		try {
			File file = new File(args[0], "Commodity.lst");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			while ((set = br.readLine()) != null) {
				String[] commoditys = set.split(",", 0);
				try {
					if (commoditys[0].matches("[0-z]{8}") && commoditys[1].matches(".*[^,].*")
							&& 2 == commoditys.length) {
						commodityNameMap.put(commoditys[0], commoditys[1]);
						System.out.println(commoditys[0] + "," + commodityNameMap.get(commoditys[0]));
						commoditySaleMap.put(commoditys[0], 0L);
					} else {
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}
				} catch (Exception e) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
				}
			}
			br.close();
		} catch (IOException e) {
			System.out.println("商品定義ファイルが存在しません");
		} finally {
			System.out.println("【商品定義ファイルの読み込みが終わりました】");
		}

		// 集計ファイル
		System.out.println("【集計】");

		File fileName = new File(args[0]);
		File fileNames[] = fileName.listFiles();
		ArrayList<File> rcdList = new ArrayList<File>();
		ArrayList<String> rcdName = new ArrayList<String>();
		ArrayList<Integer> rcdNum = new ArrayList<Integer>();
		// rcdファイル読み込み
		for (int i = 0; i < fileNames.length; i++) {
			if (fileNames[i].getName().matches("[0-9]{8}\\.rcd")) {
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
				while ((str = br.readLine()) != null) {
					rcdLine.add(str);
				}

				// 計算開始
				long branchSum;
				long commoditySum;
				if (branchSaleMap.containsKey(rcdLine.get(0))) {
					branchSum = branchSaleMap.get(rcdLine.get(0));
					branchSum += Long.parseLong(rcdLine.get(2));
					if (String.valueOf(branchSum).matches("^[0-9]{1,10}")) {
						branchSaleMap.put(rcdLine.get(0), branchSum);
					} else {
						System.out.println("合計金額が10桁を超えました");
					}

				} else {
					branchSum = Long.parseLong(rcdLine.get(2));
					branchSaleMap.put(rcdLine.get(0), branchSum);
				}

				if (commoditySaleMap.containsKey(rcdLine.get(1))) {
					commoditySum = commoditySaleMap.get(rcdLine.get(1));
					commoditySum += Long.parseLong(rcdLine.get(2));
					if (String.valueOf(commoditySum).matches("^[0-9]{1,10}")) {
						commoditySaleMap.put(rcdLine.get(1), commoditySum);
					} else {
						// System.out.println("合計金額が10桁を超えました");
					}
				} else {
					commoditySum = Long.parseLong(rcdLine.get(2));
					commoditySaleMap.put(rcdLine.get(1), commoditySum);
				}
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
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
		   // 内容を表示
			try {
			File file = new File(args[0], "branch.out");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (Entry<String,Long> bh : branchentries) {
				bw.write(bh.getKey() + "," + branchNameMap.get(bh.getKey()) + "," + bh.getValue());
				bw.newLine();
				System.out.println(bh.getKey() + "," + branchNameMap.get(bh.getKey()) + "," + bh.getValue());
			}
			bw.close();
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
		}


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
	   // 内容を表示
			try {
			File file = new File(args[0], "commodity.out");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			 for (Entry<String,Long> ch : commodityentries) {
				bw.write(ch.getKey() + "," + commodityNameMap.get(ch.getKey()) + "," + ch.getValue());
				bw.newLine();
				System.out.println(ch.getKey() + "," + commodityNameMap.get(ch.getKey()) + "," + ch.getValue());
			}
			bw.close();
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
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