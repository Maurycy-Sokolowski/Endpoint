package com.backend.api

import org.eclipse.jetty.annotations.AnnotationConfiguration
import org.eclipse.jetty.plus.webapp.EnvConfiguration
import org.eclipse.jetty.plus.webapp.PlusConfiguration
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.Configuration
import org.eclipse.jetty.webapp.FragmentConfiguration
import org.eclipse.jetty.webapp.MetaInfConfiguration
import org.eclipse.jetty.webapp.TagLibConfiguration
import org.eclipse.jetty.webapp.WebAppContext
import org.eclipse.jetty.webapp.WebInfConfiguration
import org.eclipse.jetty.webapp.WebXmlConfiguration

object EmbedMe {

  def main(args: Array[String]) {
    val port = 8080
    val server = new Server(port)
    val wardir = "target/Endpoint-1.0"
    val context = new WebAppContext()
    context.setResourceBase(wardir)
    context.setDescriptor(wardir + "WEB-INF/web.xml")
    context.setConfigurations(Array(new AnnotationConfiguration(), new WebXmlConfiguration(), new WebInfConfiguration(), new TagLibConfiguration(), new PlusConfiguration(), new MetaInfConfiguration(), new FragmentConfiguration(), new EnvConfiguration()))
    context.setContextPath("/Endpoint-1.0")
    context.setParentLoaderPriority(true)
    server.setHandler(context)
    server.start()
    server.join()
  }
}
