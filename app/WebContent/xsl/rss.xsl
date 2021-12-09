<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:template match="/">
  <xsl:apply-templates select="//channel"/>
  <table width="100%">
  	
    <xsl:apply-templates select="//item"/>
    

  </table>
</xsl:template>

<xsl:template match="channel">
    
<table width="100%" border="0" cellpadding="2" cellspacing="0">
	<tr>
		<td align="right">
			<xsl:apply-templates select="image"/>
		</td>
	</tr>
	<tr>
		<td align="left" style="font-size:12pt; font-weight:bold">
			<xsl:value-of select="title"/>
		</td>
	</tr>
	
	<tr>
		<td height="20">
			
		</td>
	</tr>

</table>

</xsl:template>


<xsl:template match="item">
    <tr><td bgcolor="#E8F2FE" style="font-size:10pt; font-weight:bold">
    
    <xsl:element name="a">
      <xsl:attribute name="href"><xsl:value-of select="link"/></xsl:attribute>
      <xsl:attribute name="target">_new</xsl:attribute>
      

      <xsl:value-of select="title" />

      
    </xsl:element>
    </td></tr>    
    
    <tr><td>
    <xsl:value-of disable-output-escaping="yes" select="description" />
    <br/><br/>
    </td></tr>
</xsl:template>

<xsl:template match="image">
  <xsl:element name="img">
    <xsl:attribute name="src"><xsl:value-of select="url"/></xsl:attribute>
  </xsl:element>
</xsl:template>

<xsl:template match="language">
</xsl:template>

</xsl:stylesheet>
