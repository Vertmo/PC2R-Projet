let usage = "usage: " ^ Sys.argv.(0) ^ " -a <server_adress> -p <server_port>"

let addr= ref "localhost"
let port = ref 1234

let speclist = [
  "-a", Arg.Set_string addr, "Server address";
  "-p", Arg.Set_int port, "Server port";
]

let main addr port _ =
  Connection.start addr port

let _ =
  Arg.parse speclist (fun u -> main !addr !port u) usage
