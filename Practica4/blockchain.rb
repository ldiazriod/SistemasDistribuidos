###########################
#	Improved version of "Build your own blockchain from scratch in 20 lines of Ruby!"	
#		from https://github.com/openblockchains/awesome-blockchains/tree/master/blockchain.rb
#  
#   and inspired by
#     Let's Build the Tiniest Blockchain In Less Than 50 Lines of Python by Gerald Nash
#     see https://medium.com/crypto-currently/lets-build-the-tiniest-blockchain-e70965a248b
#
#	Now, Blockchain with prompt transactions, transactions counter for each block, 
#											 ledger, proof of work and dynamic variable name. 
#
#	This Blockchain can be set as a loop for infinite using of the Blockchain.
#
#
#  to run use:
#    $ ruby ./blockchain.rb
#
#
#

require 'digest'    							# For hash checksum digest function SHA256
require 'pp'        							# For pp => pretty printer
# require 'pry'                     # For on the fly debugging
require_relative 'block'					# class Block
require_relative 'transaction'		# method Transactions

LEDGER = []

#####
## Blockchain building, one block at a time.
##  This will create a first block with fake transactions
## 	and then prompt user for transactions informations and set it in a new block.
## 	
## Each block can take multiple transaction
## 	when a user has finish to add transaction, 
##  the block is added to the blockchain and writen in the ledger

def create_first_block #Create first block with default info. 
	i = 0 #Instance value
	#Set first block from the blockchain with the default one.
	instance_variable_set( "@b#{i}", Block.first(
			{ from: "Dutchgrown", to: "Vincent", what: "Tulip Bloemendaal Sunset", qty: 10 }, 
			{ from: "Keukenhof", to: "Anne", what: "Tulip Semper Augustus", qty: 7 } 
		)
	)
	LEDGER << @b0 #Append to LEDGER array.
	pp @b0 #Pretty print @b0
	p "============================"
	add_block #Call to add_block function to create new blocks
end
		
def add_block #Add and create a new block
	i = 1
	loop do #While get_transaction_data (new blocks transaction interface)
		#Set next block. Point to previous block and define next block by using get_transactions_data function
		#instance_variable_set("@b#{i}", Block.next( (instance_variable_get("@b#{i-2}")), (instance_variable_get("@b#{i-1}")), get_transactions_data))
		if (i-2) >= 0 
			instance_variable_set("@b#{i}", Block.next( (instance_variable_get("@b#{i-2}")), (instance_variable_get("@b#{i-1}")), get_transactions_data))
		else
			instance_variable_set("@b#{i}", Block.next( (instance_variable_get("@b#{i-1}")), (instance_variable_get("@b#{i-1}")), get_transactions_data))
		end
		LEDGER << instance_variable_get("@b#{i}") #Store data structure on LEDGER array
		p "============================"
		pp instance_variable_get("@b#{i}") #Pretty print actual block transaction
		p "============================"
		i += 1 #Increment Instance index value.
	end
end

def launcher #Simple launcher function.
	#Display initial information and loading bar. Finally calls to create_first_block function.
	puts "==========================="
	puts ""
	puts "Welcome to Simple Blockchain In Ruby !"
	puts ""
	sleep 1.5
	puts "This program was created by Anthony Amar for and educationnal purpose"
	puts ""
	sleep 1.5
	puts "Wait for the genesis (the first block of the blockchain)"
	puts ""
	for i in 1..10
		print "."
		sleep 0.5
		break if i == 10
	end
	puts "" 
	puts "" 
	puts "==========================="
	create_first_block
end

launcher #Call launcher function.