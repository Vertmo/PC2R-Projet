(*******************************************************************************)
(*                           Connection to server                              *)
(*******************************************************************************)

exception Quit

(** Number of sent commands per second *)
let server_tickrate = 6

(** Helper function to read from socket *)
let my_input_line fd =
  let buff = (Bytes.of_string " ") and r = ref "" in
  while Unix.read fd buff 0 1 > 0 && Bytes.get buff 0 <> '\n' do
    r := !r ^ (Bytes.to_string buff)
  done;
  !r

(** Send a command to the server *)
let send s cmd =
  let msg = (Protocol.string_of_clientcmd cmd)^"\n" in
  ignore (Unix.write s (Bytes.of_string msg) 0 (String.length msg))

(* Send the position commands to the server *)
let send_commands s =
  while true do
    let (angle, thrust) = !Game.newCom in
    send s (NewPosCom (Game.state.player.coord, angle, thrust));
    Game.newCom := (0., 0);
    Thread.delay (1./.(float_of_int server_tickrate));
  done

(** Listens to the server *)
let listen_server s = try
    while true do
      let msg = my_input_line s in
      (* print_endline msg; *)
      Protocol.execute_command (Protocol.servercmd_of_string msg);
    done
  with Quit -> ()

(** Start the connection *)
let start serv port =
  let sock = Unix.socket Unix.PF_INET Unix.SOCK_STREAM 0 in
  let host = Unix.gethostbyname serv in
  let h_addr = host.Unix.h_addr_list.(0) in
  let sock_addr = Unix.ADDR_INET(h_addr,port) in
  Unix.connect sock sock_addr;
  let _ = (Thread.create listen_server sock) in
  sock

(** Close the connection *)
let close = Unix.close
