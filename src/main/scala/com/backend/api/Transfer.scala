package com.backend.api

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.PrintWriter
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import java.sql.Statement
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Iterator
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.net.ssl.HttpsURLConnection
import javax.servlet.ServletException
import javax.servlet.annotation.WebInitParam
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.googlecode.gwt.crypto.client.TripleDesCipher
import scala.collection.immutable.TreeMap
import scala.collection.mutable.ArrayBuffer
import java.util.UUID
import Transfer._
import com.netflix.astyanax.Keyspace
import com.netflix.astyanax.AstyanaxContext
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl
import com.netflix.astyanax.connectionpool.NodeDiscoveryType
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor
import com.netflix.astyanax.thrift.ThriftFamilyFactory
import com.netflix.astyanax.model.ColumnFamily
import com.netflix.astyanax.serializers.StringSerializer
import com.netflix.astyanax.connectionpool.exceptions.BadRequestException
import com.google.common.collect.ImmutableMap
import scala.collection.mutable.HashMap
import org.apache.commons.mail._
import scala.collection.JavaConverters._
import java.net.InetAddress
import com.netflix.astyanax.model.ConsistencyLevel
import net.sf.jmimemagic.Magic
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.Format
import java.awt.image.DataBufferByte
import javax.servlet.annotation.WebFilter
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.FilterConfig

object Transfer {
  val parser = new JsonParser
  val miniDB = Map(("alpha" -> ("beta", "female")), ("alpha1" -> ("beta1", "male")))

  def auth(user: String, pass: String): Boolean = {
    user.equals("alpha")
  }
}

@WebServlet(description = "Endpoint API", urlPatterns = Array("/Auth"))
class Endpoint extends HttpServlet {

  protected override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val res = new JsonObject
    res.addProperty("result", "invalid")
    val callback = request.getParameter("callback")
    try {
      if (request.getParameter("payload") != null) {
        val payload = parser.parse(request.getParameter("payload")).getAsJsonObject
        val username = payload.get("username").getAsString
        if (miniDB.contains(username)) {
          val password = payload.get("password").getAsString
          val l = miniDB(username)
          if (l._1.equals(password)) {
            res.addProperty("result", "invalid")
          }
        }
      }
    } catch {
      case e: Exception =>
    }
    response.setContentType("application/json")
    val out = response.getWriter
    out.print(callback + "(" + res + ");")
  }

  protected override def doPost(request: HttpServletRequest, response: HttpServletResponse) {
  }
}