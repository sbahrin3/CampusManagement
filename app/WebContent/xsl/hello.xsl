<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<html>
<body>
<table>
<xsl:for-each select="greeting">
<tr><td bgcolor="silver">
<b>
<xsl:value-of select="hello" />
</b>
</td></tr>
</xsl:for-each>
</table>
</body>
</html>
</xsl:template>
</xsl:stylesheet>