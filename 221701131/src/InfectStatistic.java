import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
* 对命令行参数进行操作
* @author SpringAlex
*/

public class InfectStatistic {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ParameterOption parOpt=new ParameterOption(args);
		parOpt.process();
		Reader r=new Reader(parOpt.log,parOpt.date);
		DateProcess dp=new DateProcess(r.getArrayListOfFiles(),
		parOpt.province,parOpt.out,parOpt.type);
	}

}

/**
* 对命令行参数进行操作
* @author SpringAlex
*/

class ParameterOption{
	private String[] myArgs;//用于记录命令行输入的参数
	private Lib lib;//持有Lib类
	public String log;//文件路径
	public String out;//输出文件路径
	public String date=null;//统计日期
	public int[] province=null;//统计省份
	public int[] type= null;//统计类型
	
	/**

	   * 构造函数

	   * @param args

	   *       字符串数组，用于记录命令行输入的参数
	   * @return

	   * @exception  (方法有异常的话加)

	   */
	public ParameterOption(String []args) {
		//构造函数
		myArgs=args;
		//持有lib 对象
		lib=new Lib();
		
}
	/**

	   * Process 对命令行进行处理

	   * @param 参数无

	   *      
	   * @return 无

	   */
	public void process() {
		if(myArgs.length==0) {
			//没有参数的情况
		}
		else {
			if(!myArgs[0].equals("list")) {
				System.out.println("不以list命令开头");
			}//if-end
			
			for(int i=1;i<myArgs.length;i++) {
				if(myArgs[i].startsWith("-")) {
					//System.out.println("包含命令段");
					if(myArgs[i].equals(lib.commands[0])) {
						if(i+1>=myArgs.length||myArgs[i+1].startsWith("-")) {//扫描之后的命令行(检查命令行数目)
							System.out.println("命令不符合规范");
							break;
						}
						else {
							//把后一条命令行当作日志文件路径
							log=myArgs[i+1];
						}	
					}//command-0-log
				  if(myArgs[i].equals(lib.commands[1])) {
					  if(i+1>=myArgs.length||myArgs[i+1].startsWith("-")) {//扫描之后的命令行(检查命令行数目)
							System.out.println("命令不符合规范");
							break;
						}
						else {
							//把后一条命令行当作日志文件路径
							out=myArgs[i+1];
						}	
				  }//command-1-out
				  if(myArgs[i].equals(lib.commands[2])){
					  if(i+1>=myArgs.length||myArgs[i+1].startsWith("-")) {//扫描之后的命令行(检查命令行数目)
							//可能是省略日期，则日期为最新的一天
						  date=null;
						}
					  else {
						  date=myArgs[i+1];
					  }
				  }//command-2-date
				  if(myArgs[i].equals(lib.commands[3])){
					  boolean flag=false;
					  type=new int[lib.types.length];
					  if(i+1>=myArgs.length||myArgs[i+1].startsWith("-")) {//扫描之后的命令行(检查命令行数目)
							//可能是省略，则type为所有
						  for(int t=0;t<type.length;t++) {
							  type[t]=1;
						  }
						}//if-end
					  else {
						  for(i++;i<myArgs.length;i++) {
							  if(myArgs[i].startsWith("-")) {
									flag=true;
									break;
								}
								for(int j=0;j<lib.types.length;j++) {
									//System.out.println("进入循环");
									if(myArgs[i].equals(lib.types[j])) {
										type[j]=1;
									}
								}//for-end		
							}//for-end
						  if(flag==true){
							  i--;
							  continue;
						  }
						  else {
							  break;
						  }
					  }//else-end
				  }//command-3-type的计数器
				  
				  if(myArgs[i].equals(lib.commands[4])){
					  boolean flag2=false;
					  province=new int[lib.provinces.length];
					  if(i+1>=myArgs.length||myArgs[i+1].startsWith("-")) {//扫描之后的命令行(检查命令行数目)
							//可能是省略，则province为所有
							  province[0]=1;
						}//if-end
					  else {
							for(i++;i<myArgs.length;i++) {
								for(int j=0;j<lib.provinces.length;j++) {
									if(myArgs[i].equals(lib.provinces[j])) {
										province[j]=1;
									}
								}//for-end	
								if(myArgs[i].startsWith("-")) {
									flag2=true;
									break;
								}
								
							}//for-end
							if(flag2==true){
								  i--;
								  continue;
							  }
							else {
								break;
							}
					  }//else-end
				  }//command-4-province
				}//start with "-"
			}//for loop -
			if(date==null) date=lib.lastDayFlag;
			if(type==null) type= new int[]{1,1,1,1};
			if(province==null) {
				province=new int[] {1};
			}
			
		}
	}
	
}


/**
* 对目录中的文件进行读取
* @author SpringAlex
*/

class Reader{
	private String filePath;//读取的文件路径
	private String lastdate;//统计日期
	private String[] fileLists;//所有文件列表
	private ArrayList<String> files=new ArrayList<String>();//需要的文件列表
	//记录真实log
	private Lib lib;
	/**

	   *  类外获取ArrayList类型的文件列表

	   * @param 参数无

	   *      
	   * @return 本类的files

	   */
	public ArrayList<String> getArrayListOfFiles() {
		//获取arrayList
		return files;
	}
	/**

	   *  构造函数

	   * @param fp

	   *      文件列表和统计日期
		* @param ld

	   *      统计日期
	   */
 	public Reader(String fp,String ld) {
		filePath=fp;
		lastdate=ld;
		lib=new Lib();
		this.reader_fileList();
		this.filter();
		
	}
 	/**
	   *  读取文件
	   * @param 无
	   * @return 无
	*/
	private void reader_fileList() {
		//通过filepath和lastdate确定读取的文件列表
		File file=new File(filePath);
		fileLists=file.list();
	}
	
	
	/**
	   *  选取文件
	   * @param 无
	   * @return 无
	*/
	private void filter() {
		//对文件列表进行处理
		if(lastdate.equals(null)||lastdate.equals(lib.lastDayFlag)) {
			//最新一天
			for(int i=0;i<fileLists.length;i++) {
				if(fileLists[i].contains(".log")) {
					files.add(fileLists[i]);
				}
			}
		}
		else {
			lastdate=lastdate.concat(".txt");
			for(int i=0;i<fileLists.length;i++) {
				if(fileLists[i].contains(".log")&&(fileLists[i].compareTo(lastdate)<=0)) {
					
					files.add(filePath.concat(fileLists[i]));
					//files.add(fileLists[i]);
				}
			}
		}
	}
	
	
}

/**
* 存储省份各个类型信息
* @author SpringAlex
*/
class DateMessage{
	public String province;
	public int ip;
	public int sp;
	public int cure;
	public int dead;
	
	/**
	   * 构造函数
	   * @param p
	   * 省份名   
	   */
	public DateMessage(String p) {
		province=p;
	}
}

/**
* 对文件数据进行操作
* @author SpringAlex
*/
class DateProcess{
	String outPath;//输出的文件
	private ArrayList<DateMessage> dm=new ArrayList<>();
	private ArrayList<String> files;
	private int[] types;
	private Lib lib;
	private boolean all;
	private DateMessage allDm=null;
	private String line=null;
	
	/**
	   * 构造函数
	   * @param arrFile
	   * 文件列表   
	   * @param provinces
	   * 省份列表  
	   * @return 无
	  * @param outFile
	   * 输出文件的路径，包含文件名  
	   */
	public DateProcess(ArrayList<String> arrFile,int []provinces,String out,int []arrType) {
		lib=new Lib();
		//创建类型列表
		types=arrType;
		//给输出文件赋值
		outPath=out;
		//创建省份列表
		if(provinces==null) {
			//代表为空
			System.out.print("省份为空");
		}
		else {
			if(provinces[0]==1) {
				all=true;
				allDm=new DateMessage("全国");
			}
			for(int i=1;i<provinces.length;i++) {
				if(provinces[i]==1) {//添加做好标记的省份
					DateMessage t=new DateMessage(lib.provinces[i]);
					dm.add(t);
				}	
			}
		}
		//将文件传递过来
		if(arrFile.isEmpty()==true) {
			//代表为空
			System.out.print("文件列表为空");
		}else {
			files=arrFile;
			try {
				this.lookLog();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//写入文件
		writeFile();
	}
	
	/**
	   * 对文件按行遍历
	   * @param 参数无
	   * @exception FileNotFoundException
	   * 找不到文件
	   * @exception UnsupportedEncodingException
	   * 不支持该编码格式  
	   * @return 无
	 */
	public void lookLog() throws FileNotFoundException, UnsupportedEncodingException {
		for(int i=0;i<files.size();i++) {
			//遍历文件列表
			//逐行读取文件内容
			File myFile=new File(files.get(i));
			InputStreamReader reader=new InputStreamReader(new FileInputStream(myFile),"utf-8");
			BufferedReader br=new BufferedReader(reader);
			try {
				line=br.readLine();
				while(line!=null) {
					//分析文本内容部分
					this.lookLine();
					if(all==true) {
						//表示全国都进行统计
						lookAll();
					}
					//
					line=br.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	/**
	   *  对数据行进行处理
	   * @param 参数无    
	   * @return 无
	*/
	public void lookLine() {
		//对行的信息进行检查筛选有用信息
		//1.对关键词的切割
		String[] list;
		String splitChar=" ";
		if(line.startsWith("//")) {
			//如果为仅供参考 则无需进行处理
			return ;
		}
		list=line.split(splitChar);
		for(int i=0;i<dm.size();i++) {
			
			if((dm.get(i).province).equals(list[0])) {
				//省份出现在前面
				
				
				//类型1
				if(list.length==4&&list[1].equals("新增")&&list[2].equals("感染患者")) {
					int person=Integer.parseInt(list[3].substring(0,list[3].length()-1));
					dm.get(i).ip+=person;
				}
				//类型2
				if(list.length==4&&list[1].equals("新增")&&list[2].equals("疑似患者")) {
					int person2=Integer.parseInt(list[3].substring(0,list[3].length()-1));
					dm.get(i).sp+=person2;
				}
				//类型3
				if(list.length==5&&list[1].equals("感染患者")&&list[2].equals("流入")) {
					int person3=Integer.parseInt(list[4].substring(0,list[4].length()-1));
					dm.get(i).ip-=person3;
				}
				//类型4
				if(list.length==5&&list[1].equals("疑似患者")&&list[2].equals("流入")) {
					int person4=Integer.parseInt(list[4].substring(0,list[4].length()-1));
					dm.get(i).sp-=person4;
				}
				//类型5
				if(list.length==3&&list[1].equals("死亡")) {
					int person5=Integer.parseInt(list[2].substring(0,list[2].length()-1));
					dm.get(i).ip-=person5;
					dm.get(i).dead+=person5;
				}
				//类型6
				if(list.length==3&&list[1].equals("治愈")) {
					int person6=Integer.parseInt(list[2].substring(0,list[2].length()-1));
					dm.get(i).ip-=person6;
					dm.get(i).cure+=person6;
				}
				//类型7
				if(list.length==4&&list[1].equals("疑似患者")&&list[2].equals("确诊感染")) {
					int person7=Integer.parseInt(list[3].substring(0,list[3].length()-1));
					dm.get(i).sp-=person7;
					dm.get(i).ip+=person7;
				}
				//类型8
				if(list.length==4&&list[1].equals("排除")&&list[2].equals("疑似患者")) {
					int person8=Integer.parseInt(list[3].substring(0,list[3].length()-1));
					dm.get(i).sp-=person8;
				}
			}
			
			else if(((dm.get(0).province).equals(list[0])==false)
						&&list.length>3&&(dm.get(i).province).equals(list[3])){
				//类型3
				if(list.length==5&&list[1].equals("感染患者")&&list[2].equals("流入")) {
					int personx=Integer.parseInt(list[4].substring(0,list[4].length()-1));
					dm.get(i).ip+=personx;
				}
				//类型4
				if(list.length==5&&list[1].equals("疑似患者")&&list[2].equals("流入")) {
					int personx2=Integer.parseInt(list[4].substring(0,list[4].length()-1));
					dm.get(i).sp+=personx2;
				}
				
			}
			else {
				//else 部分
			}	
		}
	}
	/**
	   *  对全国数据进行处理
	   * @param 参数无    
	   * @return 无
	*/
	public void lookAll() {
		//不区分各个省份
		//1.拆分字符串
		//2.识别语句类型进行统计
		String[] list;
		String splitChar=" ";
		if(line.startsWith("//")) {
			//如果为仅供参考 则无需进行处理
			return ;
		}
		list=line.split(splitChar);
		//类型1
		if(list.length==4&&list[1].equals("新增")&&list[2].equals("感染患者")) {
			int person=Integer.parseInt(list[3].substring(0,list[3].length()-1));
			allDm.ip+=person;
		}
		//类型2
		if(list.length==4&&list[1].equals("新增")&&list[2].equals("疑似患者")) {
			int person2=Integer.parseInt(list[3].substring(0,list[3].length()-1));
			allDm.sp+=person2;
		}
		//类型3
		/*省份之间流入流出无需统计
		if(list.length==5&&list[1].equals("感染患者")&&list[2].equals("流入")) {
			int person3=Integer.parseInt(list[4].substring(0,list[4].length()-1));
			dm.get(i).ip-=person3;
		}
		//类型4
		if(list.length==5&&list[1].equals("疑似患者")&&list[2].equals("流入")) {
			int person4=Integer.parseInt(list[4].substring(0,list[4].length()-1));
			dm.get(i).sp-=person4;
		}
		*/
		//类型5
		if(list.length==3&&list[1].equals("死亡")) {
			int person5=Integer.parseInt(list[2].substring(0,list[2].length()-1));
			allDm.ip-=person5;
			allDm.dead+=person5;
		}
		//类型6
		if(list.length==3&&list[1].equals("治愈")) {
			int person6=Integer.parseInt(list[2].substring(0,list[2].length()-1));
			allDm.ip-=person6;
			allDm.cure+=person6;
		}
		//类型7
		if(list.length==4&&list[1].equals("疑似患者")&&list[2].equals("确诊感染")) {
			int person7=Integer.parseInt(list[3].substring(0,list[3].length()-1));
			allDm.sp-=person7;
			allDm.ip+=person7;
		}
		//类型8
		if(list.length==4&&list[1].equals("排除")&&list[2].equals("疑似患者")) {
			int person8=Integer.parseInt(list[3].substring(0,list[3].length()-1));
			allDm.sp-=person8;
		}
		
	}
	/**
	   *  对文件进行处理
	   * @param 参数无    
	   * @return 无
	*/
	public void writeFile() {
		try {
			FileWriter fw=new FileWriter(outPath,false);
			//清空文件内容
			fw.write("");
			if(all==true) {
				//显示 省份 类型 人数
				fw.append(allDm.province);
				System.out.print(allDm.province);
				if(types[0]==1) {
					fw.append(" "+lib.outTypes[0]+" "+allDm.ip+"人");
					System.out.print(" "+lib.outTypes[0]+" "+allDm.ip+"人");
				}
				if(types[1]==1) {
					fw.append(" "+lib.outTypes[1]+" "+allDm.sp+"人");
					System.out.print(" "+lib.outTypes[1]+" "+allDm.sp+"人");
				}
				if(types[2]==1) {
					fw.append(" "+lib.outTypes[2]+" "+allDm.cure+"人");
					System.out.print(" "+lib.outTypes[2]+" "+allDm.cure+"人");
				}
				if(types[3]==1) {
					fw.append(" "+lib.outTypes[3]+" "+allDm.dead+"人");
					System.out.print(" "+lib.outTypes[3]+" "+allDm.dead+"人");
				}
				fw.append("\n");
				System.out.print("\n");
			}
			for(int i=0;i<dm.size();i++) {
				fw.append(dm.get(i).province);
				System.out.print(dm.get(i).province);
				if(types[0]==1) {
					fw.append(" "+lib.outTypes[0]+" "+dm.get(i).ip+"人");
					System.out.print(" "+lib.outTypes[0]+" "+dm.get(i).ip+"人");
				}
				if(types[1]==1) {
					fw.append(" "+lib.outTypes[1]+" "+dm.get(i).sp+"人");
					System.out.print(" "+lib.outTypes[1]+" "+dm.get(i).sp+"人");
				}
				if(types[2]==1) {
					fw.append(" "+lib.outTypes[2]+" "+dm.get(i).cure+"人");
					System.out.print(" "+lib.outTypes[2]+" "+dm.get(i).cure+"人");
				}
				if(types[3]==1) {
					fw.append(" "+lib.outTypes[3]+" "+dm.get(i).dead+"人");
					System.out.print(" "+lib.outTypes[3]+" "+dm.get(i).dead+"人");
				}
				fw.append("\n");
				System.out.print("\n");
			}
			fw.append("// 该文档并非真实数据，仅供测试使用");
			System.out.print("// 该文档并非真实数据，仅供测试使用");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}