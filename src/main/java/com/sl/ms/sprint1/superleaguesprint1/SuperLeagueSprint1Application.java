package com.sl.ms.sprint1.superleaguesprint1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SuperLeagueSprint1Application {

	public static void main(String[] args) {
		SpringApplication.run(SuperLeagueSprint1Application.class, args);
		final String DIRECTORY_NAME = ".";

		try {
			/*
			 * Once a file is created it will pass the file name to the readCSVFile method
			 * which will parse and read the file.
			 * 
			 */
			readCSVFile(DIRECTORY_NAME, "inventory.csv");

		} catch (Exception e) {
			System.out.println("error happened while reading file " + e.getMessage());
		}
	}

	/**
	 * This method reads the CSV file from the directory and does some processing
	 * for generating reports.
	 * 
	 * @param fileDirectory - Polling directory structure
	 * @param fileName      - Polling directory structure
	 * @throws IOException
	 */
	private static String readCSVFile(final String fileDirectory, String fileName) throws IOException {

		// fileName = "inventory.csv";
		Path path = Paths.get(fileName);
		Scanner scanner = null;
		try {
			scanner = new Scanner(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Read all the files from the file.
		Stream<String> lines = Files.lines(Paths.get(fileDirectory + "\\" + fileName));
		// create a list of inventory to hold all the items from the CSV
		List<Inventory> inventoryList = new ArrayList<Inventory>();
		String outputSummary = new String();

		// Perform the below two functions on the streams :
		// 1. Filter each information by splitting the string based on ';'
		// 2. Populate inventory objects for each line item.
		lines.map(line -> line.split(";")).forEach(value -> {
			Inventory inventory = new Inventory();
			inventory.setId(value[0]);
			inventory.setName(value[1]);
			inventory.setPrice(value[2]);
			inventory.setUnitsSold(value[3]);
			inventory.setDate(value[4]);
			inventoryList.add(inventory);
		});

		// Display stock summary per day - requirement a
		outputSummary = outputSummary + stockSummaryPerDay(inventoryList);
		// Display top 5 items in demand - requirement b
		outputSummary = outputSummary + topItems(inventoryList);
		// Display summary of items sold today - requirement c
		outputSummary = outputSummary + itemsSoldToday(inventoryList);
		// Display summary of total items sold per month - requirement d
		outputSummary = outputSummary + itemsSoldPerMonth(inventoryList);
		// Display quantity of Sale for one particular item - requirement e
		outputSummary = outputSummary + itemWiseSummaryOfSale(inventoryList);

		System.out.println(outputSummary);

		// close off the stream
		lines.close();

		return outputSummary.toString();
	}

	/**
	 * This method generates the report for the entire stock summary
	 * 
	 * @param inventoryList
	 * @return String - the generated report
	 */
	private static String stockSummaryPerDay(List<Inventory> inventoryList) {
		StringBuilder stockSummary = new StringBuilder();
		stockSummary.append("************************ STOCK SUMMARY *****************************");
		stockSummary.append("\n");
		stockSummary.append("====================================================================");
		stockSummary.append("\n");
		stockSummary.append("\tDate\t|\tItem\t|\tPrice\t|\tUnits Sold");
		stockSummary.append("\n");
		stockSummary.append("====================================================================");
		stockSummary.append("\n");
		inventoryList.remove(0);
		inventoryList.forEach(inventory -> {
			stockSummary.append("\t" + inventory.getDate() + "\t|\t" + inventory.getName() + "\t|\t"
					+ inventory.getPrice() + "\t|\t" + inventory.getUnitsSold());
			if (inventory.getId().equals("8")) {
				stockSummary.append("\n");
				stockSummary.append("-------------------------------------------------------------------");
			}
			stockSummary.append("\n");
		});

		stockSummary.append("\n");
		stockSummary.append("\n");
		return stockSummary.toString();
	}

	/**
	 * This method generates the report for the top 5 items for the month
	 * 
	 * @param inventoryList
	 * @return String - the generated report
	 */
	private static String topItems(List<Inventory> inventoryList) {
		StringBuilder topItemsSummary = new StringBuilder();
		Map<String, Integer> itemMap = new HashMap<String, Integer>();
		topItemsSummary.append("*********************** TOP ITEMS in DEC ***************************");
		topItemsSummary.append("\n");
		topItemsSummary.append("====================================================================");
		topItemsSummary.append("\n");
		topItemsSummary.append("\tRank\t|\tName\t|\tUnits Sold");
		topItemsSummary.append("\n");
		topItemsSummary.append("--------------------------------------------------------------------");
		topItemsSummary.append("\n");

		for (Inventory inventory : inventoryList) {
			if (null == itemMap.get(inventory.getName())) {
				itemMap.put(inventory.getName(), Integer.parseInt(inventory.getUnitsSold()));
			}
			Integer currentCount = itemMap.get(inventory.getName());
			itemMap.put(inventory.getName(), currentCount + Integer.parseInt(inventory.getUnitsSold()));
		}

		List<Entry<String, Integer>> intermediateList = new LinkedList<Entry<String, Integer>>(itemMap.entrySet());

		// Sorting the list based on values
		Collections.sort(intermediateList, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> count1, Entry<String, Integer> count2) {
				return count2.getValue().compareTo(count1.getValue());
			}
		});

		// Maintaining insertion order with the help of LinkedList
		int count = 1;
		Map<String, Integer> sortedItemMap = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> entry : intermediateList) {
			if (count < 6) {
				sortedItemMap.put(entry.getKey(), entry.getValue());
				topItemsSummary.append("\t" + count + "\t|\t" + entry.getKey() + "\t|\t" + entry.getValue());
				topItemsSummary.append("\n");
				count++;
			}
		}
		topItemsSummary.append("\n");
		topItemsSummary.append("\n");
		return topItemsSummary.toString();
	}

	/**
	 * This method generates the report for the items sold on a day
	 * 
	 * @param inventoryList
	 * @return String - the generated report
	 */
	private static String itemsSoldToday(List<Inventory> inventoryList) {
		StringBuilder itemsTodaySummary = new StringBuilder();
		String currentDate = "3-DEC";
		itemsTodaySummary.append("*********************** Items Sold " + currentDate + " ***************************");
		itemsTodaySummary.append("\n");
		itemsTodaySummary.append("====================================================================");
		itemsTodaySummary.append("\n");
		itemsTodaySummary.append("\tItem\t|\tPrice\t|\tUnits Sold");
		itemsTodaySummary.append("\n");
		itemsTodaySummary.append("--------------------------------------------------------------------");
		itemsTodaySummary.append("\n");
		inventoryList.forEach(inventory -> {
			if (currentDate.equalsIgnoreCase(inventory.getDate())) {
				itemsTodaySummary.append("\t" + inventory.getName() + "\t|\t" + inventory.getPrice() + "\t|\t"
						+ inventory.getUnitsSold());
				itemsTodaySummary.append("\n");
			}
		});
		itemsTodaySummary.append("--------------------------------------------------------------------");
		itemsTodaySummary.append("\n");
		itemsTodaySummary.append("\n");
		return itemsTodaySummary.toString();
	}

	/**
	 * This method generates the report for the items sold for the month
	 * 
	 * @param inventoryList
	 * @return String - the generated report
	 */
	private static String itemsSoldPerMonth(List<Inventory> inventoryList) {
		StringBuilder itemsSoldPerMonth = new StringBuilder();
		Map<String, Integer> itemMap = new HashMap<String, Integer>();
		itemsSoldPerMonth.append("******************* Total Items Sold in Dec ************************");
		itemsSoldPerMonth.append("\n");
		itemsSoldPerMonth.append("====================================================================");
		itemsSoldPerMonth.append("\n");
		itemsSoldPerMonth.append("\tItem\t|\tUnits Sold");
		itemsSoldPerMonth.append("\n");
		itemsSoldPerMonth.append("--------------------------------------------------------------------");
		itemsSoldPerMonth.append("\n");
		for (Inventory inventory : inventoryList) {
			if (null == itemMap.get(inventory.getName())) {
				itemMap.put(inventory.getName(), Integer.parseInt(inventory.getUnitsSold()));
			}
			Integer currentCount = itemMap.get(inventory.getName());
			itemMap.put(inventory.getName(), currentCount + Integer.parseInt(inventory.getUnitsSold()));
		}
		itemMap.forEach((name, value) -> {
			itemsSoldPerMonth.append("\t" + name + "\t|\t" + value);
			itemsSoldPerMonth.append("\n");
		});
		itemsSoldPerMonth.append("--------------------------------------------------------------------");
		itemsSoldPerMonth.append("\n");
		itemsSoldPerMonth.append("\n");
		return itemsSoldPerMonth.toString();
	}

	/**
	 * This method generates the report for the summary of a given item
	 * 
	 * @param inventoryList
	 * @return String - the generated report
	 */
	private static String itemWiseSummaryOfSale(List<Inventory> inventoryList) {
		StringBuilder itemWiseSummary = new StringBuilder();
		final String itemName = "item2";
		itemWiseSummary.append("******************** Summary for Item2 in Dec **********************");
		itemWiseSummary.append("\n");
		itemWiseSummary.append("====================================================================");
		itemWiseSummary.append("\n");
		itemWiseSummary.append("\tItem\t|\tDate\t|\tUnits Sold");
		itemWiseSummary.append("\n");
		itemWiseSummary.append("--------------------------------------------------------------------");
		itemWiseSummary.append("\n");

		inventoryList.stream().filter(inventory -> itemName.equals(inventory.getName())).collect(Collectors.toList())
				.forEach(inventory -> {
					itemWiseSummary.append("\t" + inventory.getName() + "\t|\t" + inventory.getDate() + "\t|\t"
							+ inventory.getUnitsSold());
					itemWiseSummary.append("\n");

				});
		itemWiseSummary.append("--------------------------------------------------------------------");
		itemWiseSummary.append("\n");
		itemWiseSummary.append("\n");
		return itemWiseSummary.toString();
	}

}