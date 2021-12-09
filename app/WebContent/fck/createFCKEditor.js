function createFCKEditor(app, name, toolbarset, height) {
var sBasePath = document.location.pathname.substring(0,document.location.pathname.lastIndexOf('_samples')) ;
var oFCKeditor = new FCKeditor( name ) ;
oFCKeditor.BasePath = '/' + app + '/fck/' ;
oFCKeditor.Height	= height ;
oFCKeditor.Value	= '' ;
oFCKeditor.ToolbarSet = toolbarset;
oFCKeditor.Create() ;
}