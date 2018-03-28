package com.pepper.learn.blockchain.controller;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.pepper.learn.blockchain.model.Block;
import com.pepper.learn.blockchain.model.Message;
import com.pepper.learn.blockchain.model.Transaction;
import com.pepper.learn.blockchain.model.TransactionParam;
import com.pepper.learn.blockchain.model.Wallet;
import com.pepper.learn.blockchain.service.BlockService;
import com.pepper.learn.blockchain.service.P2PService;

@RestController
public class BlockChainController {

	private static final Logger logger = LoggerFactory.getLogger(BlockChainController.class);

	@Autowired
	BlockService blockService;

	@Autowired
	P2PService p2pService;

	/**
	 * 1.查看区块链
	 * @return
	 */
	@RequestMapping("/chain")
	public List<Block> getBlockChain() {
		return blockService.getBlockChain();
	}

	/**
	 * 2.创建钱包
	 * @return
	 */
	@RequestMapping("/wallet/create")
	public String createWallet() {
		Wallet wallet = blockService.createWallet();
		Wallet[] wallets = { new Wallet(wallet.getPublicKey()) };
		String msg = JSON.toJSONString(new Message(P2PService.RESPONSE_WALLET, JSON.toJSONString(wallets)));
		p2pService.broatcast(msg);
		return wallet.getAddress();
	}

	/**
	 * 3.获取所有钱包
	 * @return
	 */
	@RequestMapping("/wallet/get")
	public Collection<Wallet> getAllWallets() {
		return blockService.getMyWalletMap().values();
	}

	/**
	 * 4.查询钱包余额--复杂情况查询utxo需调整
	 * @param address
	 * @return
	 */
	@RequestMapping("/wallet/balance/get")
	public int getBalance(@RequestParam("address") String address) {
		return blockService.getWalletBalance(address);
	}

	/**
	 * 5.挖矿
	 * @param address
	 * @return
	 */
	@RequestMapping("/mine")
	public String mine(@RequestParam("address") String address) {

		Wallet myWallet = blockService.getMyWalletMap().get(address);
		if (myWallet == null) {
			return "挖矿指定的钱包不存在";
		}
		Block newBlock = blockService.mine(address);
		if (newBlock == null) {
			return "挖矿失败，可能有其他节点已挖出该区块";

		}
		Block[] blocks = { newBlock };
		String msg = JSON.toJSONString(new Message(P2PService.RESPONSE_BLOCKCHAIN, JSON.toJSONString(blocks)));
		p2pService.broatcast(msg);
		return JSON.toJSONString(newBlock);
	}

	/**
	 * 6.转账交易--复杂情况需调整
	 * @param txParam
	 * @return
	 */
	@RequestMapping("/transactions/new")
	public String createTransaction(@RequestBody TransactionParam txParam) {
		Wallet senderWallet = blockService.getMyWalletMap().get(txParam.getSender());
		Wallet recipientWallet = blockService.getMyWalletMap().get(txParam.getRecipient());
		if (recipientWallet == null) {
			recipientWallet = blockService.getOtherWalletMap().get(txParam.getRecipient());
		}
		if (senderWallet == null || recipientWallet == null) {
			return "钱包不存在";
		}

		Transaction newTransaction = blockService.createTransaction(senderWallet, recipientWallet, txParam.getAmount());
		if (newTransaction == null) {
			return "钱包" + txParam.getSender() + "余额不足或该钱包找不到一笔大于等于" + txParam.getAmount() + "BTC的UTXO";
		} else {
			Transaction[] txs = { newTransaction };
			String msg = JSON.toJSONString(new Message(P2PService.RESPONSE_TRANSACTION, JSON.toJSONString(txs)));
			p2pService.broatcast(msg);
			return JSON.toJSONString(newTransaction);
		}
	}

	/**
	 * 7.获取当前节点未打包的交易
	 * @return
	 */
	@RequestMapping("/transactions/unpacked/get")
	public String getUnpackedTransactions() {
		List<Transaction> transactions = new ArrayList<>(blockService.getAllTransactions());
		transactions.removeAll(blockService.getPackedTransactions());
		return JSON.toJSONString(transactions);
	}

	/**
	 * 8.查询所有socket节点
	 */
	@RequestMapping("/peers")
	public void getAllPeers() {
        for (WebSocket socket : p2pService.getSockets()) {
            InetSocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
            logger.info(remoteSocketAddress.getHostName() + ":" + remoteSocketAddress.getPort() + "  ");
        }
    
	}

}
