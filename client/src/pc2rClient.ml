open Protocol

let usage = "usage: " ^ Sys.argv.(0) ^ " -a <server_adress> -p <server_port>"

(* Constants *)
let refresh_tickrate = 60

let addr= ref "localhost"
let port = ref 1234

let speclist = [
  "-a", Arg.Set_string addr, "Server address";
  "-p", Arg.Set_int port, "Server port";
]

let main addr port username =
  let s = Connection.start addr port in
  Connection.send s (Connect username);
  while true do
    Thread.delay (1./.(float_of_int refresh_tickrate));
  done

let _ =
  Arg.parse speclist (fun u -> main !addr !port u) usage
