package wisoft.pack.actions;

/**
 * Interface defining the application's command IDs.
 * Key bindings can be defined for specific commands.
 * To associate an action with a command, use IAction.setActionDefinitionId(commandId).
 *
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {

    public static final String CMD_OPEN = "WiosftUpdatePack.open";
    public static final String CMD_OPEN_MESSAGE = "WiosftUpdatePack.openMessage";
    public static final String OPEN_NEWPACK_DLG="WiosftUpdatePack.new";
    public static final String DEL_PACKINFO="WiosftUpdatePack.delPack";
    
}
