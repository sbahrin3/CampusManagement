import lebah.util.*
import java.util.*
import java.text.*
import lebah.db.*
import java.sql.*
import educate.dms.*

println "command = " + command

def is_login = req.session.getAttribute("_portal_islogin")
context.put("is_login", is_login)

switch ( command ) {


		
	default:
		use_view = "login.vm"
		break
}

