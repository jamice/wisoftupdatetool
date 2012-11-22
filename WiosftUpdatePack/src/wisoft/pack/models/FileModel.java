package wisoft.pack.models;

import java.io.File;
import java.util.List;
public class FileModel extends Model {
	public static enum EditType { NORMAL,UPDATE,DELETE };
	private File file;
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	private EditType edittype =EditType.NORMAL;
	public EditType getEdittype() {
		return edittype;
	}
	public void setEdittype(EditType edittype) {
		this.edittype = edittype;
	}
	
	
	public FileModel(File file)
	{
		this.file = file;
	}
	
	@Override
	public List<Model> getChildren() {
		// TODO Auto-generated method stub
		File[] files =file.listFiles();
		if(files!=null)
		{
			for(File zfile:files)
			{
				addChild(new FileModel(zfile));
			}
		}
		return super.getChildren();
	}
}