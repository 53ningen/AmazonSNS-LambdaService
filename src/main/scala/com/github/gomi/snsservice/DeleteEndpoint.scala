package com.github.gomi.snsservice

import java.util.logging.Logger

import com.amazonaws.auth.{AWSCredentials, BasicAWSCredentials}
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.{DeleteEndpointRequest, GetEndpointAttributesRequest}
import com.typesafe.config.ConfigFactory
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.collection.JavaConversions._

object DeleteEndpoint extends RequestHandler[SNSEvent, Unit] {

  private type EndpointArn = String

  private val logger = Logger.getGlobal
  private val conf = ConfigFactory.load()
  private lazy val credential: AWSCredentials = new BasicAWSCredentials(conf.getString("amazon_sns.access_key"), conf.getString("amazon_sns.access_secret"))
  private lazy val snsClient: AmazonSNSClient = new AmazonSNSClient(credential).withRegion(Region.getRegion(Regions.fromName(conf.getString("amazon_sns.region"))))

  override def handleRequest(input: SNSEvent, context: Context) = {
    val records = input.getRecords.toList
    records.foreach(handle)
  }

  def handle(record: SNSEvent.SNSRecord) = {
    val message = record.getSNS.getMessage
    if (message != null) {
      getEndpointArn(message).foreach { endpointArn =>
        logger.info(s"check if enabled: endpoint_arn = $endpointArn")
        val enabled = getEnabled(endpointArn).getOrElse(true)
        if (!enabled) {
          logger.info(s"delete disabled endpoint: endpoint_arn = $endpointArn")
          deleteEndpoint(endpointArn)
        }
      }
    } else {
      logger.warning(s"receive empty message: message_id = ${record.getSNS.getMessageId}")
    }
  }

  def getEndpointArn(message: String): Option[EndpointArn] = {
    val resources: List[String] = for {
      JObject(child) <- parse(message)
      JField("EndpointArn", JString(resource)) <- child
    } yield resource
    resources.headOption
  }

  def getEnabled(endpointArn: EndpointArn): Option[Boolean] = {
    val request = new GetEndpointAttributesRequest().withEndpointArn(endpointArn)
    try {
      val result = snsClient.getEndpointAttributes(request)
      result.getAttributes.toMap.get("Enabled").map(_ == "true")
    } catch {
      case e: Throwable =>
        e.printStackTrace()
        None
    }
  }

  def deleteEndpoint(endpointArn: EndpointArn) = {
    val request = new DeleteEndpointRequest().withEndpointArn(endpointArn)
    try {
      snsClient.deleteEndpoint(request)
    } catch {
      case e: Throwable => e.printStackTrace()
    }
  }

}
