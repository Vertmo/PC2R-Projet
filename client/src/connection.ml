(******************************************************************************)
(*                           Connection to server                             *)
(******************************************************************************)

exception Quit

(** Helper function to read from socket *)
let my_input_line fd =
  let buff = (Bytes.of_string " ") and r = ref "" in
  while Unix.read fd buff 0 1 > 0 && Bytes.get buff 0 <> '\n' do
    r := !r ^ (Bytes.to_string buff)
  done;
  !r

(** Handle an established connection *)
let handle_connection s _ = try
    while true do
      let si = (read_line ())^"\n" in
      ignore (Unix.write s (Bytes.of_string si) 0 (String.length si));
      let so = (my_input_line s) in
      print_endline so;
    done
    with Quit -> ()

(** Start the connection *)
let start serv port =
  let sock = Unix.socket Unix.PF_INET Unix.SOCK_STREAM 0 in
  let host = Unix.gethostbyname serv in
  let h_addr = host.Unix.h_addr_list.(0) in
  let sock_addr = Unix.ADDR_INET(h_addr,port) in
  Unix.connect sock sock_addr;
  handle_connection sock sock_addr;
  Unix.close sock
