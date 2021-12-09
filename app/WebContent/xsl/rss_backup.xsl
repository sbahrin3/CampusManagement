<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:template match="/">
  <xsl:apply-templates select="//channel"/>
  <ul>
    <xsl:apply-templates select="//item"/>
  </ul>
</xsl:template>

<xsl:template match="channel">
    
<xsl:apply-templates select="image"/>
<b><font size="4"><xsl:value-of select="title"/></font></b><br/>
<b><font size="2"><xsl:value-of select="description"/></font></b>
<br/>
</xsl:template>

<xsl:template match="item">
  <li>
    <xsl:element name="a">
      <xsl:attribute name="href"><xsl:value-of select="link"/></xsl:attribute>
      <b><xsl:value-of select="title" /></b><br/>
    </xsl:element>
    <xsl:value-of disable-output-escaping="yes" select="description" /><br/><br/>
  </li>
</xsl:template>

<xsl:template match="image">
  <xsl:element name="img">
    <xsl:attribute name="src"><xsl:value-of select="url"/></xsl:attribute>
    <xsl:attribute name="style">float:left; padding: 10px;</xsl:attribute>
  </xsl:element>
</xsl:template>

<xsl:template match="language">
</xsl:template>

</xsl:stylesheet>
