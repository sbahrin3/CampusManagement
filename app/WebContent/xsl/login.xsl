<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<body bgcolor="yellow">
				<form name="loginform" method="post" action="/rnd/login">
					<xsl:choose>
						<xsl:when test="login_page/login_count &gt; 0">
							Number of attempted login = <xsl:value-of select="login_page/login_count" />
						</xsl:when>
						<xsl:otherwise>
						</xsl:otherwise>
					</xsl:choose>
					<table>
						<tr>
							<td>Username :</td>
							<td>
								<input name="username" >
									<xsl:attribute name="value">
										<xsl:value-of select="login_page/user_id" />
									</xsl:attribute>
								</input>
							</td>
						</tr>
						<tr>
							<td>Password :</td>
							<td>
								<input type="password" name="password"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td>
								<input type="submit" value="Login" />
							</td>
						</tr>
					</table>
				</form>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>