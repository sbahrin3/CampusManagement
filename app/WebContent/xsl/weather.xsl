<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:template match="/">
	<p>
  	<xsl:apply-templates select="//loc"/>
  	</p>
  	<p>
  	<xsl:apply-templates select="//cc"/>
  	</p>
</xsl:template>

<xsl:template match="loc">


</xsl:template>

<xsl:template match="cc">
	Location: <xsl:value-of select="obst" /><br/>
	Time: <xsl:value-of select="lsup" /><br/>
	Temperature: <xsl:value-of select="tmp" /> C<br/>
	Feels Like: <xsl:value-of select="flik" /> C<br/>
	Condition: <xsl:value-of select="t" />
  	<xsl:element name="img">
    	<xsl:attribute name="src">../weather/icons/<xsl:value-of select="icon"/>.png</xsl:attribute>
    	<xsl:attribute name="style">float:center; padding: 10px;</xsl:attribute>
  	</xsl:element>	

</xsl:template>

</xsl:stylesheet>
