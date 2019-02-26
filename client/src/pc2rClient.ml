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

  (* Initializing *)
  Connection.send s (Connect username);
  Game.state.player.username <- username;

  (* Redefine signal so that ctrl-c in the terminal exits the game gracefully *)
  ignore (Sys.signal Sys.sigint
            (Sys.Signal_handle (fun _ -> Connection.send s (Exit username);
                                 exit 0)));

  while true do
    Thread.delay (1./.(float_of_int refresh_tickrate));
  done

let _ =
  Arg.parse speclist (fun u -> main !addr !port u) usage
