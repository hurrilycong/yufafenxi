package yufafenxi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class Yufafenxi {
	
	private static boolean leftBracket = false;
	
	private static boolean judgeForASN = false;
	
	private static boolean judgeNum = false;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		HashMap<String, Integer> keyvalue = new HashMap<>();
		keyvalue = readFileByLines("keyword.txt");
		
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		System.out.println("请输入一个表达式");
		while (input.hasNext()) {
			String string = input.nextLine();
			String[] innerNum = fenxi(string, keyvalue).concat("#").split(" ");
			//语法分析过程
			if (yuFaFenXi(innerNum)) {
				System.out.println("表达式正确");
			}
		}	
	}
	
	/*
	 * 判断使用AddOrSub或乘除
	 * 
	 */
	public static boolean judge(String aString)
	{
		return isAddOrSub(aString) || isMulOrDiv(aString);
	}
	
	/*
	 * 将类号转换为字符
	 * 
	 */
	public static String change(String aString)
	{
		HashMap<String, Integer> keyvalue = new HashMap<>();
		keyvalue = readFileByLines("keyword.txt");
		Set<Entry<String, Integer>> set = keyvalue.entrySet();
		Iterator<Entry<String, Integer>> iterator = set.iterator();
		while (iterator.hasNext()) {
			HashMap.Entry<String, Integer> entry = (HashMap.Entry<String, Integer>)iterator.next();
			if (entry.getValue().equals(Integer.parseInt(aString))) {
				return (String)entry.getKey();
			}
		}
		return "未找到此类号代表的关键字。";
	}
	
	/*
	 * 表达式分析
	 * 
	 */
	public static boolean Expression(ArrayDeque<String> stack)
	{
		String aString = "";
		aString = stack.pop();
		if (isNotSharp(aString)) {
			if (judge(aString)) {//加减
				if (judgeForASN) {
					System.out.println("表达式错误。错误为:"+change(aString));
					return false;
				}
				if (judgeNum) {
					judgeNum = false;
				}
				judgeForASN = true;
				if (!Expression(stack)) {
					return false;
				}
				return true;
			}
			else if (aString.equals("9") || aString.equals("10")){//整数或变量
				if (judgeNum) {
					System.out.println("表达式错误。错误为:"+change(aString));
					return false;
				}
				if (judgeForASN) {
					judgeForASN = false;
				}
				judgeNum = true;
				if (!Expression(stack)) {
					return false;
				}
				return true;
			}
			else if (aString.equals("19")) {//左括号情况
				if (isMulOrDiv(stack.getFirst())) {
					System.out.println("表达式错误，在'('后面。是:"+change(stack.pop()));
					return false;
				}
				leftBracket = true;//置已读左括号为true
				if (!Expression(stack)) {
					return false;
				}
				return true;
			}
			else if (aString.equals("20") && leftBracket == true) {
				leftBracket = false;
				if (!Expression(stack)) {
					return false;
				}
				return true;
			}
			else {
				System.out.println("表达式错误，错误符号为:"+change(aString));//此处需要修改，这里输出的是类号
				return false;
			}
		}
		else {
			//检查结束
			return true;
		}
	}
	/*
	 * 语法分析过程
	 * 
	 */
	public static boolean yuFaFenXi(String[] innerNum)
	{
		ArrayDeque<String> stack = new ArrayDeque<>();
		for (int i = innerNum.length-1;i >= 0;i--) {
			stack.push(innerNum[i]);
		}
		String aString = stack.getFirst();
		if (isMulOrDiv(aString)) {
			System.out.println("表达式错误，错误为第一个符号。"+change(aString));
			return false;
		}
		return Expression(stack);
	}
	/*
	 * 判断是否为#号
	 * 
	 */
	public static boolean isNotSharp(String op)
	{
		if (op.equals("#")) {
			return false;
		}
		else {
			return true;
		}
	}
	/*
	 * 判断是否为乘除
	 * 
	 */
	public static boolean isMulOrDiv(String op)
	{
		if (op.equals("13") || op.equals("14")) {
			return true;
		}
		else {
			return false;
		}
	}
	/*
	 * 判断是否为加减
	 */
	public static boolean isAddOrSub(String op)
	{
		if (op.equals("11") || op.equals("12")) {
			return true;
		}
		else {
			return false;
		}
	}
	/*
	 * 以行为单位读取文件
	 * 读取类号的文件操作
	 * 
	 */
 	public static HashMap<String, Integer> readFileByLines(String filename)
	{
		File file = new File(filename);
		BufferedReader reader = null;
		HashMap<String, Integer> keyvalue = new HashMap<String, Integer>();
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while((tempString = reader.readLine()) != null)
			{
				String[] line = tempString.split(" ");
				try
				{
					int a = Integer.parseInt(line[0]);
					keyvalue.put(line[1], a);
				}
				catch (Exception exception)
				{
					exception.printStackTrace();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return keyvalue;
	}
	
	/*
	 * 分析过程
	 * 
	 */
	public static String fenxi(String string, HashMap<String, Integer> keyvalue)
	{
		String returnString = new String();
		int a = 0;
		boolean judge = false;
		
		String str = new String();
		for(int i = 0;i < string.length();i++)
		{
			if(isLetter(string.charAt(i)))
			{
				str = str.concat(String.valueOf(string.charAt(i)));
			}
			else if (isNumber(string.charAt(i))) {
				int n = i+1;
				for(;n < string.length();i++)
				{
					if (!isNumber(string.charAt(n))) {
						break;
					}
				}
				if (n == i+1) {
					returnString = returnString.concat(String.valueOf(10)+' ');
				}
				else {
					//String number = string.substring(i, n);
					returnString = returnString.concat(String.valueOf(10)+' ');
					i = n-1;
				}
			}
			else if (string.charAt(i) == ' ') {
				if (!str.equals("") && (a = searchHashMap(str, keyvalue)) != 0) {
					returnString = returnString.concat(String.valueOf(a)+' ');
				}
				else if (isVariable(str)) {
					returnString = returnString.concat(String.valueOf(9)+' ');
				}
				else {
					for(int i1 = 0;i1 < str.length();i1++)
					{
						if (isNumber(str.charAt(i1))) {
							judge = true;
						}
						else {
							judge = false;
							System.out.println("不能识别:"+str);
						}
					}
					if (judge) {
						judge = false;
						returnString = returnString.concat(String.valueOf(10)+' ');
					}
					str = "";
				}
			}
			else if (isOperator1(string.charAt(i))) {
				if (!str.equals("") && (a = searchHashMap(str, keyvalue)) != 0) {
					returnString = returnString.concat(String.valueOf(a)+' ');
				}
				else if (isVariable(str)) {
					returnString = returnString.concat(String.valueOf(9)+' ');
				}
				else {
					for(int i3 = 0;i3 < str.length();i++){
						if (isNumber(str.charAt(i3))) {
							judge = true;
						}
						else {
							judge = false;
							System.out.println("不能识别:"+str);
						}
					}
					if (judge) {
						judge = false;
						returnString = returnString.concat(String.valueOf(10)+' ');
					}
				}
				if (i != string.length()-1 && isOperator2(string.charAt(i+1))) {
					String string2 = string.substring(i, i+2);
					if ((a = searchHashMap(string2, keyvalue)) != 0) {
						returnString = returnString.concat(String.valueOf(a)+' ');
					}
					else {
						System.out.println("发生错误。");
					}
				}
				returnString = returnString.concat(String.valueOf(searchHashMap(String.valueOf(string.charAt(i)), keyvalue))+' ');
				str = "";
			}
		}
		return returnString;
	}
	
	/*
	 * searchHashMap
	 * 搜索HashMap中的value
	 * 
	 */
	public static int searchHashMap(String string, HashMap<String, Integer> keyvalue)
	{
		Object object = null;
		int a = 0;
		if((object = keyvalue.get(string)) != null)
		{
			a = (int)object;
		}
		return a;
	}
	
	/*
	 * 判断是否是变量名
	 * 
	 */
	public static boolean isVariable(String string)
	{
		if (string.equals("")) {
			return false;
		}
		if (isLetter(string.charAt(0)) || string.charAt(0) == '_') {
			for(int i = 1;i < string.length();i++)
			{
				if (!(isLetter(string.charAt(i)) || isNumber(string.charAt(i)) || string.charAt(i) == '_')) {
					return false;
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * isLetter
	 * 判断是否为字母
	 * 
	 */
	public static boolean isLetter(char ch)
	{
		if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/*
	 * isNumber
	 * 判断是否是数字
	 * 
	 */
	public static boolean isNumber(char ch)
	{
		if (ch >= '0' && ch <= '9') {
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * isOperator1
	 * 判断是否为运算符
	 * 
	 */
	public static boolean isOperator1(char ch)
	{
		if (ch == '=' || ch == '<' || ch == '>') {
			return true;
		}
		else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
			return true;
		}
		else if (ch == '!' || ch == '|' || ch == '%' || ch == '&') {
			return true;
		}
		else if (ch == '(' || ch == ')' || ch == '{' || ch == '}') {
			return true;
		}
		else if (ch == ';') {
			return true;
		}
		return false;
	}
	
	/*
	 * isOperator2
	 * 判断第二个字符是否是运算符
	 * 
	 */
	public static boolean isOperator2(char ch)
	{
		if (ch == '=' || ch == '<' || ch == '>') {
			return true;
		}
		else if (ch == '+' || ch == '-') {
			return true;
		}
		else if (ch == '|' || ch == '&') {
			return true;
		}
		else if (ch == ';') {
			return true;
		}
		return false;
	}
}