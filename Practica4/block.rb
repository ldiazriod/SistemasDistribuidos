class Block #Blockchain class
  #Define class attributes on only read (attr_reader)
  # index -> block index. timestamp -> Date the block was created. Transactions -> list of transactions data
  # transactions_count -> number of transactions. previous_hash -> hash code from the previous block.
  # nonce -> unique identifier number used to autenticate one transaction. hash -> hash code for the actual block.
  attr_reader :index, :timestamp, :transactions, 
							:transactions_count, :previous_hash, :pre_previous_hash,
							:nonce, :hash 

  # Block constructor/initializer
  def initialize(index, transactions, pre_previous_hash, previous_hash)
    @index         		 	 = index #Block index
    @timestamp      	 	 = Time.now #Actual date
    @transactions 	 		 = transactions #Previous transactions
		@transactions_count  = transactions.size #Size of the list
    @previous_hash 		 	 = previous_hash #Previous hash code
    @pre_previous_hash   = pre_previous_hash
    @nonce, @hash  		 	 = compute_hash_with_proof_of_work
  end 

	def compute_hash_with_proof_of_work(difficulty="00")
		nonce = 0
		loop do 
			hash = calc_hash_with_nonce(nonce)
			if hash.start_with?(difficulty)
				return [nonce, hash]
			else
				nonce +=1
			end
		end
	end

  #Create hash block.
  def calc_hash_with_nonce(nonce=0)
    sha = Digest::SHA256.new #SHA256 encryption.
    #Add all attributes to the variable.
    sha.update( nonce.to_s + 
								@index.to_s + 
								@timestamp.to_s + 
								@transactions.to_s + 
								@transactions_count.to_s +	
								@previous_hash +
                @pre_previous_hash
              )
    sha.hexdigest #Encrypt on hexadecimal
  end

  def self.first( *transactions )    # Create genesis block
    ## Uses index zero (0) and arbitrary previous_hash ("0")
    Block.new( 0, transactions, "0", "0" )
  end

  def self.next( pre_previous, previous, transactions )
    if previous.index == 0
      Block.new( pre_previous.index+1, transactions, pre_previous.hash, previous.hash)
    else
      Block.new( pre_previous.index+2, transactions, pre_previous.hash, previous.hash)
    end
  end
end  # class Block