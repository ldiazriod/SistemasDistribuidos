#Transaction interface
require_relative 'wallet'
def get_transactions_data
	transactions_block ||= []
	blank_transaction = Hash[from: "", to: "", what: "", qty: ""]
	loop do #Request transaction info from user input
		puts "" 
		puts "Enter your wallet id for the new transaction"
		from = gets.chomp
		puts "" 
		puts "What do you want to send ?"
		what = gets.chomp
		puts "" 
		puts "How much quantity ?"
		qty  = gets.chomp
		puts "" 
		puts  "Enter target wallet id for the new transaction"
		to 	 = gets.chomp

		#Add testing value Wallet.setWallet(from, testing) 
		fromWallet = Wallet.setWallet(from, to);
		transaction = Hash[from: fromWallet.from_wallet, to: "#{to}", what: "#{what}", qty: "#{qty}"]
		if fromWallet.to_wallet != to 
			puts "ERROR: Incorrect target wallet"
			break
		end
		transactions_block << transaction
		puts "" 
		puts "Do you want to make another transaction for this block ? (Y/n)"
		new_transaction = gets.chomp.downcase

		if new_transaction == "y"
			self
		else
			return transactions_block
			break
		end
	end
end

#009b2267cc8051cc2f4a1e4e5ae00d8b315c0f12aa2188bd9cc9f0433b076e8a
#009b2267cc8051cc2f4a1e4e5ae00d8b315c0f12aa2188bd9cc9f0433b076e8a