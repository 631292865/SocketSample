package com.chni.bp88a_server.server;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import com.chni.bp88a_server.controller.BP88AController;
import com.chni.bp88a_server.model.SocketRequest;
import com.chni.bp88a_server.model.SocketResponse;
import com.chni.bp88a_server.utils.HexUtils;

public class TcpServer extends IoHandlerAdapter {

	public static final int PORT = 9088;
	private static final Logger log = Logger.getLogger(TcpServer.class);
	
	public TcpServer() throws IOException {
		
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		IoBuffer bbuf = (IoBuffer) message;
		byte[] byten = new byte[bbuf.limit()];
		bbuf.get(byten, bbuf.position(), bbuf.limit());
		log.info("收到消息：" + HexUtils.bytes2HexString(byten));
		//1.获取客户端传过来的数据
		String strRead = HexUtils.bytes2HexString(byten);
		SocketRequest req = new SocketRequest(strRead, strRead.length()/2);
		SocketResponse resp = new SocketResponse();
		//2.调用控制器处理数据
		BP88AController c = new BP88AController();
		c.execute(req, resp);
		//3.处理响应数据
		log.info("返回的数据" + Arrays.toString(resp.getData()));
		IoBuffer buffer = IoBuffer.allocate(24);
		buffer.put(resp.getData());
		buffer.flip();
		session.write(buffer);
	}


	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		log.info("会话关闭");
		if(session!=null){
			session.close(true);
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		super.exceptionCaught(session, cause);
		log.info("会话异常");
	}

	@Override
	public void messageSent(IoSession iosession, Object obj) throws Exception {
		super.messageSent(iosession, obj);
		log.info("服务端消息发送");
	}

	@Override
	public void sessionCreated(IoSession iosession) throws Exception {
		super.sessionCreated(iosession);
		log.info("会话创建");
	}

	@Override
	public void sessionIdle(IoSession iosession, IdleStatus idlestatus)
			throws Exception {
		super.sessionIdle(iosession, idlestatus);
//		log.info("会话休眠");
	}

	@Override
	public void sessionOpened(IoSession iosession) throws Exception {
		super.sessionOpened(iosession);
		log.info("会话打开");
	}

}
