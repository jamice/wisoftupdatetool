package wisoft.pack.dialogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;

import wisoft.pack.edits.XmlSqlEditorInput;
import wisoft.pack.edits.sql.SQLDocumentProvider;
import wisoft.pack.events.ZipHandleEvent;
import wisoft.pack.events.ZipHandleEventListener;
import wisoft.pack.models.FileModel;
import wisoft.pack.models.PackInfoModel;
import wisoft.pack.models.PackRelyModel;
import wisoft.pack.utils.UpdateInfo;
import wisoft.pack.utils.ZipUtil;
import wisoft.pack.views.Console;
import wisoft.pack.views.Console.ConsoleType;

import com.wisoft.wims.WimsSingleIssueTracking;

public class ExportPackWizard extends Wizard {
	private PackInfoModel pack;
	private ExportPackWizardPage page;
	public ExportPackWizard(PackInfoModel pack) {
		setWindowTitle("即将导出一个更新包");
		this.pack = pack;
		this.page = new ExportPackWizardPage(pack);
	}

	@Override
	public void addPages() {
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		
		
		final String exportpath  = this.page.text.getText()+"/"+this.page.text_1.getText();
		final boolean isClassic = this.page.button_2.getSelection();
		if(exportpath.trim().isEmpty())
		{
			page.setErrorMessage("请选择一个路径来保存导出的更新包"); 
			return false;
		}
		Console.getInstance().print("导出更新包开始……", pack.getName(), Console.ConsoleType.INFO);	
		Job job = new Job("导出更新包") {
			
			private void printlnToConsole(final String msg,final ConsoleType type)
			{
				Display.getDefault().asyncExec(new Runnable() {                        
	    			public void run() {                                                                                    
	    				Console.getInstance();
	    				Console.print(msg, pack.getName(), type);
	    				//nv.addPackInfo(pack);
	    			}});
			}
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				monitor.beginTask("导出开始",  IProgressMonitor.UNKNOWN);
				ZipUtil zip = new ZipUtil(exportpath,"GBK");
				zip.addZipEventListener(new ZipHandleEventListener(){
					@Override
					public void ZipHandle(ZipHandleEvent me) {
						monitor.worked(me.curFileNum);
						monitor.setTaskName(me.curFileName);
						try {
							Thread.sleep(80);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				try {
					
					File sqlfile = new File(pack.getSavePath()+File.separator+XmlSqlEditorInput.TYPE_SQL);
					if(!sqlfile.exists())
					{
						Reader in= new BufferedReader(new InputStreamReader(SQLDocumentProvider.class.getResourceAsStream("sql_template.sql")));
						FileWriter writer = new FileWriter(sqlfile);
						try {
							StringBuffer buffer= new StringBuffer(512);
							char[] readBuffer= new char[512];
							int n= in.read(readBuffer);
							while (n > 0) {
								buffer.append(readBuffer, 0, n);
								n= in.read(readBuffer);
							}
							writer.write(buffer.toString());
						} finally {
							in.close();
							writer.close();
						}
					}
//					try {
//			            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
//			            FileWriter writer = new FileWriter(sqlfile, true);
//			            writer.write("\r\n insert into system_version_info (ID, MODULENAME, MODULECODE, VERSION, PUBLISH_DATE, UPDATE_DATE, REMARK)");
//			            writer.write("select '',");
//			            writer.write("'"+pack.getModuleName()+"',");
//			            writer.write("'"+pack.getModuleCode()+"',");
//			            writer.write("'"+pack.getVersion()+"',");
//			            writer.write("'"+pack.getCreateTime()+"',");
//			            writer.write("to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),");
//			            writer.write("'"+pack.getKeyWord()+"' from dual;");
//			            
//			            writer.close();
//			        } catch (IOException e) {
//			            e.printStackTrace();
//			        }
			        if(isClassic)
			        {	
			        	zip.appendText(pack.getName()+"_RealseNote.txt", createRealseNote());
			        	zip.appendFile(pack.getSavePath()+File.separator+UpdateInfo.UpdateDirName,"");
			        	zip.appendFile(pack.getSavePath()+File.separator+XmlSqlEditorInput.TYPE_SQL, pack.getName()+"_DataBase.sql");
			        }
			        else
			        {
			        	zip.appendFile(pack.getSavePath(),"");
			        }
					zip.close();
					printlnToConsole("导出更新包完成！",ConsoleType.INFO);
					monitor.done();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					printlnToConsole(e.toString(),ConsoleType.ERROR);;
				}
				return Status.OK_STATUS;
			}
			
		};
		job.setUser(true);
		job.schedule();
		return true;
	}
	
	private String createRealseNote()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sb = new StringBuffer();
		sb.append("************************************************************************\r\n");
		sb.append("                           ★"+pack.getName()+"发布说明★\r\n");
		sb.append("    平台名称：  "+pack.getModuleName()+"\r\n");
		sb.append("    平台代码：  "+pack.getModuleCode()+"\r\n");
		sb.append("    平台版本：  "+pack.getVersion()+"\r\n");
		sb.append("    创建日期：  "+pack.getCreateTime()+"\r\n");
		sb.append("    创建人：       "+pack.getCreateMan()+"\r\n");
		sb.append("    发布日期：  "+df.format(new Date())+"\r\n");
		sb.append("    关键字：       "+pack.getKeyWord()+"\r\n");
		sb.append("************************************************************************\r\n");
		sb.append("\r\n");
		sb.append("【更新范围】\r\n");
		sb.append(pack.getScopeString());
		sb.append("\r\n");
		sb.append("\r\n");
		sb.append("【更新说明】\r\n");
		sb.append("   "+pack.getReleaseNote()+"\r\n");
		sb.append("\r\n");
		sb.append("【手动配置说明】\r\n");
		List<FileModel> conffiles = pack.getConfFiles();
		for(int i=0;i<conffiles.size();i++)
		{
			sb.append("   "+(i+1)+"、"+"〖"+conffiles.get(i).getConftype()+"〗 路径为： " +conffiles.get(i).getFullPath()+"\r\n");
			sb.append("       〖修改方法〗   "+conffiles.get(i).getContent()+"\r\n");
		}
		
		sb.append("\r\n");
		sb.append("【更新依赖】\r\n");
		List<PackRelyModel> relys = pack.getPackRelys();
		for(int i=0;i<relys.size();i++)
		{
			sb.append("   "+(i+1)+"、"+relys.get(i).toString()+"     发布日期："+relys.get(i).getPublishTime()+"\r\n");
		}
		
		sb.append("\r\n");
		sb.append("【修正问题单】\r\n");
		List<WimsSingleIssueTracking> trackrelys = pack.getTrackRelys();
		for(int i=0;i<trackrelys.size();i++)
		{
			sb.append("   "+(i+1)+"、 问题单号："+trackrelys.get(i).getLsh()+"     申请人："+trackrelys.get(i).getSqpersonid()
					+"      所属项目："+trackrelys.get(i).getProid()+"\r\n");
			sb.append("     问题单内容："+trackrelys.get(i).getContent()+"\r\n");
		}
		sb.append("\r\n");
		
		return sb.toString();
	}

}
