switch ( command ) {
case "doneUploadFiles": 
	doneUploadFiles()
	break
case "uploadAgain":
	use_view = "upload_form.vm"
	break
default:
	use_view = "upload_form.vm"
}

def doneUploadFiles() {
	println "Done uploading files."
	def files = req.uploadedFiles //get the uploaded files
	def filenames = new ArrayList() //to put only filenames (without complete path)
	for ( filename in files) {
		println filename
		filenames.add(filename.substring(filename.lastIndexOf("/")+1))
	}
	context.put("filenames", filenames)
	use_view = "done_upload.vm"
}